package com.swp391.cclearly.dto.refund;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundResponse {
  private UUID refundId;
  private UUID orderId;
  private String orderCode;
  private String customerName;
  private String customerPhone;
  private String customerEmail;
  private String status; // PENDING, APPROVED, REJECTED, COMPLETED
  private String type; // return / refund
  private BigDecimal refundAmount;
  private String reason;
  private Instant requestDate;
  private Instant processedDate;
  private List<RefundItemResponse> items;

  @Data
  @Builder
  public static class RefundItemResponse {
    private String name;
    private int quantity;
    private BigDecimal price;
  }
}
