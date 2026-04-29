package com.swp391.cclearly.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "Payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "payment_id")
  private UUID paymentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @Column(name = "method", length = 50)
  private String method;

  @Column(name = "amount", precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(name = "status", length = 50)
  private String status;

  @Column(name = "payos_order_code", length = 50)
  private String payosOrderCode;
}
