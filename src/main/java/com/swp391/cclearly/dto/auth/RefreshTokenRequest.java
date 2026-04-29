package com.swp391.cclearly.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body làm mới access token")
public class RefreshTokenRequest {

  @Schema(description = "Refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Refresh token không được để trống")
  private String refreshToken;
}
