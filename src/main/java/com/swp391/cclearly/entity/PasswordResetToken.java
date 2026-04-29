package com.swp391.cclearly.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "token_id")
  private UUID tokenId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "token", nullable = false, length = 255)
  private String token;

  @Column(name = "expired_at", nullable = false)
  private Instant expiredAt;

  @Column(name = "used", nullable = false)
  @Builder.Default
  private Boolean used = false;

  @Column(name = "created_at", nullable = false)
  @Builder.Default
  private Instant createdAt = Instant.now();

  public boolean isExpired() {
    return Instant.now().isAfter(expiredAt);
  }

  public boolean isValid() {
    return !used && !isExpired();
  }
}
