package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.inventory.ImportStockRequest;
import com.swp391.cclearly.dto.inventory.InventoryResponse;
import com.swp391.cclearly.entity.InventoryStock;
import com.swp391.cclearly.entity.InventoryStockId;
import com.swp391.cclearly.entity.OrderItem;
import com.swp391.cclearly.entity.ProductVariant;
import com.swp391.cclearly.entity.StockMovement;
import com.swp391.cclearly.entity.Warehouse;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.InventoryStockRepository;
import com.swp391.cclearly.repository.ProductVariantRepository;
import com.swp391.cclearly.repository.StockMovementRepository;
import com.swp391.cclearly.repository.WarehouseRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

  private final InventoryStockRepository inventoryStockRepository;
  private final ProductVariantRepository productVariantRepository;
  private final WarehouseRepository warehouseRepository;
  private final StockMovementRepository stockMovementRepository;
  private final AuditLogService auditLogService;

  public ApiResponse<List<InventoryResponse>> getInventory(String search, UUID warehouseId) {
    List<InventoryStock> allStocks = inventoryStockRepository.findAll();

    Map<UUID, List<InventoryStock>> grouped = allStocks.stream()
        .filter(stock -> warehouseId == null || stock.getWarehouse().getWarehouseId().equals(warehouseId))
        .collect(Collectors.groupingBy(stock -> stock.getVariant().getVariantId()));

    List<InventoryResponse> response = new ArrayList<>();
    for (var entry : grouped.entrySet()) {
      List<InventoryStock> stocks = entry.getValue();
      ProductVariant variant = stocks.get(0).getVariant();

      if (search != null && !search.isBlank()) {
        String q = search.toLowerCase();
        boolean matches = (variant.getProduct().getName() != null
            && variant.getProduct().getName().toLowerCase().contains(q))
            || (variant.getSku() != null && variant.getSku().toLowerCase().contains(q));
        if (!matches) {
          continue;
        }
      }

      int totalStock = stocks.stream()
          .mapToInt(s -> s.getQuantityOnHand() != null ? s.getQuantityOnHand() : 0)
          .sum();

      List<InventoryResponse.WarehouseStock> warehouseStocks = stocks.stream()
          .map(s -> InventoryResponse.WarehouseStock.builder()
              .warehouseId(s.getWarehouse().getWarehouseId())
              .warehouseName(s.getWarehouse().getName())
              .quantityOnHand(s.getQuantityOnHand() != null ? s.getQuantityOnHand() : 0)
              .locationCode(s.getLocationCode())
              .build())
          .collect(Collectors.toList());

      response.add(InventoryResponse.builder()
          .variantId(variant.getVariantId())
          .productName(variant.getProduct().getName())
          .variantSku(variant.getSku())
          .colorName(variant.getColorName())
          .productType(variant.getProduct().getCategoryType())
          .price(variant.getSalePrice() != null ? variant.getSalePrice() : variant.getProduct().getBasePrice())
          .totalStock(totalStock)
          .warehouseStocks(warehouseStocks)
          .build());
    }

    return ApiResponse.success("Lay danh sach ton kho thanh cong", response);
  }

  @Transactional
  public void reserveStockForOrderItems(Collection<OrderItem> orderItems, String reason) {
    Map<UUID, Integer> requiredQuantities = aggregateRequiredQuantities(orderItems);

    for (Map.Entry<UUID, Integer> entry : requiredQuantities.entrySet()) {
      ProductVariant variant = productVariantRepository.findById(entry.getKey())
          .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay bien the san pham"));

      int required = entry.getValue();
      if (required <= 0) {
        continue;
      }

      List<InventoryStock> stocks = inventoryStockRepository.findByVariant_VariantId(entry.getKey()).stream()
          .sorted(Comparator.comparing(stock -> stock.getWarehouse().getName()))
          .collect(Collectors.toList());

      int available = stocks.stream()
          .mapToInt(stock -> stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0)
          .sum();

      if (available < required) {
        throw new BadRequestException(buildOutOfStockMessage(variant, available, required));
      }

      int remaining = required;
      for (InventoryStock stock : stocks) {
        if (remaining == 0) {
          break;
        }

        int onHand = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
        if (onHand <= 0) {
          continue;
        }

        int deducted = Math.min(onHand, remaining);
        stock.setQuantityOnHand(onHand - deducted);
        inventoryStockRepository.save(stock);
        recordStockMovement(variant, -deducted, reason);
        remaining -= deducted;
      }
    }
  }

  @Transactional
  public void releaseStockForOrderItems(Collection<OrderItem> orderItems, String reason) {
    Map<UUID, Integer> releaseQuantities = aggregateRequiredQuantities(orderItems);

    for (Map.Entry<UUID, Integer> entry : releaseQuantities.entrySet()) {
      ProductVariant variant = productVariantRepository.findById(entry.getKey())
          .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay bien the san pham"));

      int quantity = entry.getValue();
      if (quantity <= 0) {
        continue;
      }

      List<InventoryStock> stocks = inventoryStockRepository.findByVariant_VariantId(entry.getKey()).stream()
          .sorted(Comparator.comparing(stock -> stock.getWarehouse().getName()))
          .collect(Collectors.toList());

      InventoryStock stock = stocks.stream().findFirst()
          .orElseGet(() -> createStockForVariant(variant));

      int onHand = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
      stock.setQuantityOnHand(onHand + quantity);
      inventoryStockRepository.save(stock);
      recordStockMovement(variant, quantity, reason);
    }
  }

  @Transactional
  public ApiResponse<InventoryResponse> importStock(ImportStockRequest request) {
    ProductVariant variant = productVariantRepository.findById(request.getVariantId())
        .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay bien the san pham"));
    Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
        .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay kho"));

    InventoryStockId stockId = new InventoryStockId(warehouse.getWarehouseId(), variant.getVariantId());

    InventoryStock stock = inventoryStockRepository.findById(stockId)
        .orElse(InventoryStock.builder()
            .id(stockId)
            .warehouse(warehouse)
            .variant(variant)
            .quantityOnHand(0)
            .build());

    int currentQuantity = stock.getQuantityOnHand() != null ? stock.getQuantityOnHand() : 0;
    stock.setQuantityOnHand(currentQuantity + request.getQuantity());
    inventoryStockRepository.save(stock);

    recordStockMovement(variant, request.getQuantity(), request.getReason() != null ? request.getReason() : "IMPORT");

    List<InventoryStock> variantStocks = inventoryStockRepository.findAll().stream()
        .filter(s -> s.getVariant().getVariantId().equals(variant.getVariantId()))
        .collect(Collectors.toList());

    int totalStock = variantStocks.stream()
        .mapToInt(s -> s.getQuantityOnHand() != null ? s.getQuantityOnHand() : 0)
        .sum();

    List<InventoryResponse.WarehouseStock> warehouseStocks = variantStocks.stream()
        .map(s -> InventoryResponse.WarehouseStock.builder()
            .warehouseId(s.getWarehouse().getWarehouseId())
            .warehouseName(s.getWarehouse().getName())
            .quantityOnHand(s.getQuantityOnHand() != null ? s.getQuantityOnHand() : 0)
            .locationCode(s.getLocationCode())
            .build())
        .collect(Collectors.toList());

    InventoryResponse response = InventoryResponse.builder()
        .variantId(variant.getVariantId())
        .productName(variant.getProduct().getName())
        .variantSku(variant.getSku())
        .colorName(variant.getColorName())
        .productType(variant.getProduct().getCategoryType())
        .price(variant.getSalePrice())
        .totalStock(totalStock)
        .warehouseStocks(warehouseStocks)
        .build();

    auditLogService.log(
        "IMPORT_STOCK",
        "Nhap " + request.getQuantity() + " " + variant.getProduct().getName()
            + " (SKU: " + variant.getSku() + ") vao kho " + warehouse.getName());
    return ApiResponse.success("Nhap kho thanh cong", response);
  }

  private Map<UUID, Integer> aggregateRequiredQuantities(Collection<OrderItem> orderItems) {
    Map<UUID, Integer> quantities = new HashMap<>();

    for (OrderItem item : orderItems) {
      int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
      addVariantQuantity(quantities, item.getVariant(), quantity);
      addVariantQuantity(quantities, item.getLensVariant(), quantity);
    }

    return quantities;
  }

  private void addVariantQuantity(Map<UUID, Integer> quantities, ProductVariant variant, int quantity) {
    if (variant == null || quantity <= 0 || Boolean.TRUE.equals(variant.getIsPreorder())) {
      return;
    }

    quantities.merge(variant.getVariantId(), quantity, Integer::sum);
  }

  private InventoryStock createStockForVariant(ProductVariant variant) {
    Warehouse warehouse = warehouseRepository.findAll().stream()
        .sorted(Comparator.comparing(Warehouse::getName))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay kho de hoan ton"));

    InventoryStockId stockId = new InventoryStockId(warehouse.getWarehouseId(), variant.getVariantId());
    return InventoryStock.builder()
        .id(stockId)
        .warehouse(warehouse)
        .variant(variant)
        .quantityOnHand(0)
        .build();
  }

  private void recordStockMovement(ProductVariant variant, int quantity, String reason) {
    StockMovement movement = StockMovement.builder()
        .variant(variant)
        .quantity(quantity)
        .reason(reason)
        .build();
    stockMovementRepository.save(movement);
  }

  private String buildOutOfStockMessage(ProductVariant variant, int available, int required) {
    String productName = variant != null
        && variant.getProduct() != null
        && variant.getProduct().getName() != null
        && !variant.getProduct().getName().isBlank()
        ? variant.getProduct().getName()
        : "Sản phẩm";

    String sku = variant != null && variant.getSku() != null && !variant.getSku().isBlank()
        ? variant.getSku()
        : "N/A";

    return "Tồn kho không đủ cho sản phẩm " + productName
        + " (SKU: " + sku + "). Chỉ còn " + available + ", yêu cầu " + required + ".";
  }
}
