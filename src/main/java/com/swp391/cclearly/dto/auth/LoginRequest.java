package com.swp391.cclearly.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body để đăng nhập")
public class LoginRequest {

  @Schema(description = "Email đăng nhập", example = "admin@cclearly.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không hợp lệ")
  private String email;

  @Schema(description = "Mật khẩu", example = "Abc@12345", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Mật khẩu không được để trống")
  private String password;
}
