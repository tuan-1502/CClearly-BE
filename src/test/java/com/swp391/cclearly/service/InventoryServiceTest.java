package com.swp391.cclearly.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.swp391.cclearly.entity.InventoryStock;
import com.swp391.cclearly.entity.OrderItem;
import com.swp391.cclearly.entity.Product;
import com.swp391.cclearly.entity.ProductVariant;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.repository.InventoryStockRepository;
import com.swp391.cclearly.repository.ProductVariantRepository;
import com.swp391.cclearly.repository.StockMovementRepository;
import com.swp391.cclearly.repository.WarehouseRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

  @Mock private InventoryStockRepository inventoryStockRepository;
  @Mock private ProductVariantRepository productVariantRepository;
  @Mock private WarehouseRepository warehouseRepository;
  @Mock private StockMovementRepository stockMovementRepository;
  @Mock private AuditLogService auditLogService;

  @InjectMocks private InventoryService inventoryService;

  @Test
  void reserveStockForOrderItems_whenInsufficient_shouldThrowBadRequest() {
    ProductVariant variant = ProductVariant.builder()
        .variantId(UUID.randomUUID())
        .sku("SKU-LOW")
        .product(Product.builder().name("Item").basePrice(BigDecimal.TEN).build())
        .build();
    OrderItem orderItem = OrderItem.builder()
        .variant(variant)
        .quantity(2)
        .build();
    InventoryStock stock = InventoryStock.builder()
        .variant(variant)
        .quantityOnHand(1)
        .build();

    when(productVariantRepository.findById(variant.getVariantId())).thenReturn(Optional.of(variant));
    when(inventoryStockRepository.findByVariantIdForUpdate(variant.getVariantId()))
        .thenReturn(List.of(stock));

    assertThatThrownBy(() -> inventoryService.reserveStockForOrderItems(List.of(orderItem), "ORDER_CREATE"))
        .isInstanceOf(BadRequestException.class);
  }
}
