package com.swp391.cclearly.dto.order;

import java.util.UUID;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
  // Address fields — required when addressId is not provided
  private String recipientName;
  private String phone;
  private String street;
  private String city;

  private String notes;

  // Optional: reuse saved address (skips recipientName/phone/street/city validation)
  private UUID addressId;

  private String paymentMethod; // cod, payos

  private String paymentType; // DEPOSIT (50%) or FULL (100%) — for preorder

  private String couponCode;

  // Optional: checkout only selected cart items
  private List<UUID> cartItemIds;
}
