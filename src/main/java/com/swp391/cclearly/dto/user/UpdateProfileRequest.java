package com.swp391.cclearly.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
  @Size(max = 100, message = "Ho ten khong duoc qua 100 ky tu")
  private String fullName;

  @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "So dien thoai khong hop le")
  private String phoneNumber;
}
