package com.swp391.cclearly.dto.admin;

import java.util.Map;
import lombok.Data;

@Data
public class UpdateSettingsRequest {
  private Map<String, String> settings;
}
