package com.swp391.cclearly.dto.admin;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLogPageResponse {
  private List<AuditLogResponse> items;
  private Meta meta;

  @Data
  @Builder
  public static class Meta {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
  }
}
