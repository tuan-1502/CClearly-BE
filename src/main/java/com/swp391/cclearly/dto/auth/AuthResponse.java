package com.swp391.cclearly.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response trả về khi đăng nhập/đăng ký thành công")
public class AuthResponse {

  @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;

  @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String refreshToken;

  @Schema(description = "Loại token", example = "Bearer")
  private String tokenType;

  @Schema(description = "Thời gian hết hạn (seconds)", example = "86400")
  private long expiresIn;

  @Schema(description = "Thông tin người dùng")
  private UserInfo user;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "Thông tin người dùng")
  public static class UserInfo {
    @Schema(description = "ID người dùng", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Email", example = "user@example.com")
    private String email;

    @Schema(description = "Họ và tên", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "Số điện thoại", example = "0901234567")
    private String phoneNumber;

    @Schema(description = "Vai trò", example = "CUSTOMER")
    private String role;

    @Schema(description = "Email đã xác thực chưa")
    private boolean isEmailVerified;
  }
}
