package com.swp391.cclearly.dto.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
  private long totalOrders;
  private long totalCustomers;
  private long totalProducts;
  private BigDecimal totalRevenue;
  private long pendingOrders;
  private long processingOrders;
  private long deliveredOrders;
  private long cancelledOrders;
  private List<RevenueByMonth> revenueByMonth;
  private Map<String, Long> ordersByStatus;
  private List<TopProduct> topProducts;

  @Data
  @Builder
  public static class RevenueByMonth {
    private String month;
    private BigDecimal revenue;
    private long orders;
  }

  @Data
  @Builder
  public static class TopProduct {
    private String name;
    private String type;
    private long sold;
    private BigDecimal revenue;
  }
}
