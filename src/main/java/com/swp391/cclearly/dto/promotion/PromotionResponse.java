package com.swp391.cclearly.dto.promotion;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromotionResponse {
  private UUID promotionId;
  private String code;
  private String discountType; // PERCENT, FIXED
  private BigDecimal value;
  private String description;
  private BigDecimal minOrder;
  private BigDecimal maxDiscount;
  private Integer usageLimit;
  private long usageCount;
  private Boolean isActive;
}
