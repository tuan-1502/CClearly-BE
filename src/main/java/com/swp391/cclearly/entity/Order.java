package com.swp391.cclearly.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "order_id")
  private UUID orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "code", length = 20)
  private String code;

  @Column(name = "status", length = 50)
  private String status;

  @Column(name = "final_amount", precision = 19, scale = 2)
  private BigDecimal finalAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coupon_id", referencedColumnName = "promotion_id")
  private Promotion coupon;

  @Column(name = "discount_amount", precision = 19, scale = 2)
  private BigDecimal discountAmount;

  @Column(name = "tracking_number", length = 100)
  private String trackingNumber;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @Column(name = "is_preorder")
  private Boolean isPreorder;

  @Column(name = "preorder_deadline")
  private LocalDate preorderDeadline;

  @Column(name = "payment_type", length = 20)
  private String paymentType; // DEPOSIT or FULL

  @Column(name = "shipping_fee", precision = 19, scale = 2)
  private BigDecimal shippingFee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id")
  private Address address;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<OrderItem> orderItems = new HashSet<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Payment> payments = new HashSet<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private Set<Refund> refunds = new HashSet<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private Set<OrderStatusLog> statusLogs = new HashSet<>();
}
