package com.swp391.cclearly.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body đăng nhập bằng Google")
public class GoogleLoginRequest {

  @Schema(description = "ID Token từ Google OAuth2", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Google ID Token không được để trống")
  private String idToken;
}
