package com.swp391.cclearly.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không hợp lệ")
  private String email;

  @NotBlank(message = "Họ tên không được để trống")
  private String fullName;

  private String phoneNumber;

  @NotBlank(message = "Mật khẩu không được để trống")
  private String password;

  @NotBlank(message = "Vai trò không được để trống")
  private String role; // ADMIN, MANAGER, SALES_STAFF, OPERATION_STAFF, CUSTOMER
}
