package com.swp391.cclearly.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
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
@Schema(description = "Request body để đăng ký tài khoản mới")
public class RegisterRequest {

  @Schema(description = "Email đăng ký", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không hợp lệ")
  private String email;

  @Schema(description = "Mật khẩu (6-100 ký tự)", example = "Abc@12345", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Mật khẩu không được để trống")
  @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự")
  private String password;

  @Schema(description = "Họ và tên", example = "Nguyễn Văn A", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Họ tên không được để trống")
  @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
  @JsonAlias({"name"})
  private String fullName;

  @Schema(description = "Số điện thoại", example = "0901234567")
  @Size(max = 20, message = "Số điện thoại không được quá 20 ký tự")
  @JsonAlias({"phone"})
  private String phoneNumber;
}
