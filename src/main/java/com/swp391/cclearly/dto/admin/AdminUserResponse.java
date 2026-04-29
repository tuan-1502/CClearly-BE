package com.swp391.cclearly.dto.admin;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserResponse {
  private UUID userId;
  private String email;
  private String fullName;
  private String phoneNumber;
  private String role;
  private String status;
  private Boolean isEmailVerified;
  private Instant createdAt;
  private Instant lastLogin;
}
