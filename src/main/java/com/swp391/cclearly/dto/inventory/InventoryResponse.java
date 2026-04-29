package com.swp391.cclearly.dto.inventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponse {
  private UUID variantId;
  private String productName;
  private String variantSku;
  private String colorName;
  private String productType;
  private BigDecimal price;
  private int totalStock;
  private List<WarehouseStock> warehouseStocks;

  @Data
  @Builder
  public static class WarehouseStock {
    private UUID warehouseId;
    private String warehouseName;
    private int quantityOnHand;
    private String locationCode;
  }
}
