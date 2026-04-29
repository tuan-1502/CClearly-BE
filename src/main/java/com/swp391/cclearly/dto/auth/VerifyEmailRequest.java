package com.swp391.cclearly.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body xác thực email bằng OTP")
public class VerifyEmailRequest {

  @Schema(description = "Email cần xác thực", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không hợp lệ")
  private String email;

  @Schema(description = "Mã OTP 6 số", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Mã OTP không được để trống")
  @Size(min = 6, max = 6, message = "Mã OTP phải có 6 ký tự")
  private String otpCode;
}
