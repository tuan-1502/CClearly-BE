package com.swp391.cclearly.dto.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {
  private UUID cartId;
  private List<CartItemResponse> items;
  private BigDecimal totalAmount;
  private int totalItems;

  @Data
  @Builder
  public static class CartItemResponse {
    private UUID cartItemId;
    private UUID variantId;
    private UUID lensVariantId;
    private String productName;
    private String lensProductName;
    private String variantSku;
    private String lensVariantSku;
    private String colorName;
    private String productType;
    private Float refractiveIndex;
    private Float lensRefractiveIndex;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
    private Boolean isPreorder;
  }
}
