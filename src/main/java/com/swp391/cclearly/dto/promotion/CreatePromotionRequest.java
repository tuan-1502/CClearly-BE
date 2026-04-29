package com.swp391.cclearly.dto.promotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreatePromotionRequest {

  @NotBlank(message = "Mã giảm giá không được để trống")
  private String code;

  @NotBlank(message = "Loại giảm giá không được để trống")
  private String discountType; // PERCENT, FIXED

  @NotNull(message = "Giá trị giảm giá không được để trống")
  private BigDecimal value;

  private String description;
  private BigDecimal minOrder;
  private BigDecimal maxDiscount;
  private Integer usageLimit;
  private Boolean isActive;
}
