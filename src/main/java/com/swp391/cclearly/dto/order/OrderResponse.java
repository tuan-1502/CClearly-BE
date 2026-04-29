package com.swp391.cclearly.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
  private UUID orderId;
  private UUID userId;
  private String code;
  private String status;
  private String type; // regular, prescription
  private BigDecimal finalAmount;
  private BigDecimal shippingFee;
  private String customerEmail;
  private String shippingStreet;
  private String shippingCity;
  private String shippingPhone;
  private String recipientName;
  private String trackingNumber;
  private String notes;
  private String paymentMethod;
  private BigDecimal paidAmount;   // sum of COMPLETED payments
  private BigDecimal codAmount;    // finalAmount - paidAmount (amount to collect on delivery)
  private Boolean isPreorder;
  private LocalDate preorderDeadline;
  private String paymentType; // DEPOSIT or FULL
  private String couponCode;
  private BigDecimal discountAmount;
  private Instant createdAt;
  private List<OrderItemResponse> items;

  @Data
  @Builder
  public static class OrderItemResponse {
    private UUID orderItemId;
    private String productName;
    private String variantSku;
    private String colorName;
    private String productType;
    private Float refractiveIndex;
    private BigDecimal unitPrice;
    private int quantity;
    private String imageUrl;
    private PrescriptionInfo prescription;
  }

  @Data
  @Builder
  public static class PrescriptionInfo {
    private String imageUrl;
    private BigDecimal sphOd;
    private BigDecimal cylOd;
    private Integer axisOd;
    private BigDecimal addOd;
    private BigDecimal sphOs;
    private BigDecimal cylOs;
    private Integer axisOs;
    private BigDecimal addOs;
    private BigDecimal pd;
    private String validationStatus;
    private String salesNote;
  }
}
