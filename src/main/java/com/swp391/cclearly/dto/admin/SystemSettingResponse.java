package com.swp391.cclearly.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemSettingResponse {
  private String key;
  private String value;
  private String group;
}
