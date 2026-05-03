package com.swp391.cclearly.dto.order;

import java.util.UUID;
import lombok.Data;

@Data
public class BuyNowOrderRequest {
  // Product selection
  private UUID variantId;
  private UUID productId;
  private UUID lensVariantId;
  private Integer quantity;

  // Address fields — required when addressId is not provided
  private String recipientName;
  private String phone;
  private String street;
  private String city;

  private String notes;

  // Optional: reuse saved address
  private UUID addressId;

  private String paymentMethod; // cod, payos
  private String paymentType; // DEPOSIT or FULL
  private String couponCode;
}
