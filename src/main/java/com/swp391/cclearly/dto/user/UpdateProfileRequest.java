package com.swp391.cclearly.dto.user;

import lombok.Data;

@Data
public class UpdateProfileRequest {
  private String fullName;
  private String phoneNumber;
}
