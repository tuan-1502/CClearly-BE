package com.swp391.cclearly.dto.admin;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLogResponse {
  private UUID logId;
  private String userId;
  private String userName;
  private String action;
  private String details;
  private String oldValue;
  private String newValue;
  private String ipAddress;
  private Instant createdAt;
}
