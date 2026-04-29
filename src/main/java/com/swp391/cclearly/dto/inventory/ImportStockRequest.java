package com.swp391.cclearly.dto.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class ImportStockRequest {

  @NotNull(message = "ID biến thể không được để trống")
  private UUID variantId;

  @NotNull(message = "ID kho không được để trống")
  private UUID warehouseId;

  @Min(value = 1, message = "Số lượng phải >= 1")
  private int quantity;

  private String reason;
}
