package com.swp391.cclearly.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response chuẩn cho tất cả API")
public class ApiResponse<T> {

  @Schema(description = "Trạng thái request", example = "true")
  private boolean success;

  @Schema(description = "Thông báo", example = "Thành công")
  private String message;

  @Schema(description = "Dữ liệu trả về")
  private T data;

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data);
  }

  public static <T> ApiResponse<T> success(String message) {
    return new ApiResponse<>(true, message, null);
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "Thành công", data);
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null);
  }

  public static <T> ApiResponse<T> error(String message, T data) {
    return new ApiResponse<>(false, message, data);
  }
}
