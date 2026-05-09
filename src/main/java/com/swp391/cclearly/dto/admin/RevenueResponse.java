package com.swp391.cclearly.dto.admin;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevenueResponse {
  private BigDecimal totalRevenue;
  private BigDecimal thisMonthRevenue;
  private BigDecimal lastMonthRevenue;
  private BigDecimal thisQuarterRevenue;
  private BigDecimal lastQuarterRevenue;
  private double growthPercent;
  private double quarterGrowthPercent;
  private List<DailyRevenue> revenueByDay;

  @Data
  @Builder
  public static class DailyRevenue {
    private String date;
    private BigDecimal revenue;
    private long orders;
  }
}
