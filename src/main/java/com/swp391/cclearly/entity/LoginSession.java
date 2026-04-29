package com.swp391.cclearly.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "login_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginSession {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "session_id")
  private UUID sessionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "refresh_token", nullable = false, length = 500)
  private String refreshToken;

  @Column(name = "device_info", length = 255)
  private String deviceInfo;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  @Column(name = "expired_at", nullable = false)
  private Instant expiredAt;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(name = "created_at", nullable = false)
  @Builder.Default
  private Instant createdAt = Instant.now();

  @Column(name = "last_accessed_at")
  private Instant lastAccessedAt;

  public boolean isExpired() {
    return Instant.now().isAfter(expiredAt);
  }

  public boolean isValid() {
    return isActive && !isExpired();
  }
}
