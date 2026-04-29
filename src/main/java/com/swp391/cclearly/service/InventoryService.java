package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.inventory.ImportStockRequest;
import com.swp391.cclearly.dto.inventory.InventoryResponse;
import com.swp391.cclearly.entity.InventoryStock;
import com.swp391.cclearly.entity.InventoryStockId;
import com.swp391.cclearly.entity.ProductVariant;
import com.swp391.cclearly.entity.StockMovement;
import com.swp391.cclearly.entity.Warehouse;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.InventoryStockRepository;
import com.swp391.cclearly.repository.ProductVariantRepository;
import com.swp391.cclearly.repository.StockMovementRepository;
import com.swp391.cclearly.repository.WarehouseRepository;
import java.util.ArrayList;
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

    // Group by variant
    Map<UUID, List<InventoryStock>> grouped = allStocks.stream()
        .filter(stock -> {
          if (warehouseId != null) {
            return stock.getWarehouse().getWarehouseId().equals(warehouseId);
          }
          return true;
        })
        .collect(Collectors.groupingBy(stock -> stock.getVariant().getVariantId()));

    List<InventoryResponse> response = new ArrayList<>();
    for (var entry : grouped.entrySet()) {
      List<InventoryStock> stocks = entry.getValue();
      ProductVariant variant = stocks.get(0).getVariant();

      // Search filter
      if (search != null && !search.isBlank()) {
        String q = search.toLowerCase();
        boolean matches = (variant.getProduct().getName() != null &&
            variant.getProduct().getName().toLowerCase().contains(q))
            || (variant.getSku() != null && variant.getSku().toLowerCase().contains(q));
        if (!matches) continue;
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

    return ApiResponse.success("Lấy danh sách tồn kho thành công", response);
  }

  @Transactional
  public ApiResponse<InventoryResponse> importStock(ImportStockRequest request) {
    ProductVariant variant = productVariantRepository.findById(request.getVariantId())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm"));
    Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho"));

    InventoryStockId stockId = new InventoryStockId(warehouse.getWarehouseId(), variant.getVariantId());

    InventoryStock stock = inventoryStockRepository.findById(stockId)
        .orElse(InventoryStock.builder()
            .id(stockId)
            .warehouse(warehouse)
            .variant(variant)
            .quantityOnHand(0)
            .build());

    stock.setQuantityOnHand(stock.getQuantityOnHand() + request.getQuantity());
    inventoryStockRepository.save(stock);

    // Record stock movement
    StockMovement movement = StockMovement.builder()
        .variant(variant)
        .quantity(request.getQuantity())
        .reason(request.getReason() != null ? request.getReason() : "IMPORT")
        .build();
    stockMovementRepository.save(movement);

    // Build response for just this variant
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
            .quantityOnHand(s.getQuantityOnHand())
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

    auditLogService.log("IMPORT_STOCK",
        "Nhập " + request.getQuantity() + " " + variant.getProduct().getName()
            + " (SKU: " + variant.getSku() + ") vào kho " + warehouse.getName());
    return ApiResponse.success("Nhập kho thành công", response);
  }
}
