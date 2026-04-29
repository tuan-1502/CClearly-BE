package com.swp391.cclearly.dto.user;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
  private UUID userId;
  private String email;
  private String fullName;
  private String phoneNumber;
  private Boolean isEmailVerified;
  private String role;
}
