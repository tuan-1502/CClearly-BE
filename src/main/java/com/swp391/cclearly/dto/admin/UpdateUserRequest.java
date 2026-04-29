package com.swp391.cclearly.dto.admin;

import lombok.Data;

@Data
public class UpdateUserRequest {
  private String fullName;
  private String phoneNumber;
  private String role;
  private String status; // ACTIVE, INACTIVE, BANNED
}
