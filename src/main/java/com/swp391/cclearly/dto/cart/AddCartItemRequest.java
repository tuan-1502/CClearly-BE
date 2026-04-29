package com.swp391.cclearly.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class AddCartItemRequest {
  // Either variantId or productId must be provided
  private UUID variantId;

  // Fallback for products without variants (e.g. accessories)
  private UUID productId;

  @NotNull(message = "Số lượng không được để trống")
  @Min(value = 1, message = "Số lượng phải ít nhất là 1")
  private Integer quantity;

  // Optional: for products that require a lens
  private UUID lensVariantId;
}
