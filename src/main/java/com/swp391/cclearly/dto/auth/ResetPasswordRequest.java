package com.swp391.cclearly.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body đặt lại mật khẩu mới")
public class ResetPasswordRequest {

  @Schema(description = "Token đặt lại mật khẩu (nhận qua email)", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Token không được để trống")
  private String token;

  @Schema(description = "Mật khẩu mới (6-100 ký tự)", example = "NewPass@123", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Mật khẩu mới không được để trống")
  @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự")
  private String newPassword;
}
