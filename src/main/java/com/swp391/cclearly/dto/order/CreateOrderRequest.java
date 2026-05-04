package com.swp391.cclearly.dto.order;

import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateOrderRequest {
  // Address fields — required when addressId is not provided
  private String recipientName;
  private String phone;
  private String street;
  private String city;

  private String notes;

  // Optional: reuse saved address (skips recipientName/phone/street/city validation)
  private UUID addressId;

  @Pattern(
      regexp = "^(?i)(COD|PAYOS|BANKING|BANK_TRANSFER|BANKTRANSFER|ONLINE)$",
      message = "Phuong thuc thanh toan khong hop le")
  private String paymentMethod; // cod, payos

  @Pattern(regexp = "^(?i)(DEPOSIT|FULL)$", message = "Loai thanh toan khong hop le")
  private String paymentType; // DEPOSIT (50%) or FULL (100%) — for preorder

  private String couponCode;

  // Optional: checkout only selected cart items
  private List<UUID> cartItemIds;
}
