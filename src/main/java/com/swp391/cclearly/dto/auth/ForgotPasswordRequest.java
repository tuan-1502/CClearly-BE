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
@Schema(description = "Request body yêu cầu đặt lại mật khẩu")
public class ForgotPasswordRequest {

  @Schema(description = "Email đăng ký tài khoản", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không hợp lệ")
  private String email;

  @Schema(description = "Domain frontend để tạo link reset password", example = "http://localhost:3000")
  private String domain;
}
