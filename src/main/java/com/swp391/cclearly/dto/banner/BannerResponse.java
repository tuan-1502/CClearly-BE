package com.swp391.cclearly.dto.banner;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BannerResponse {
  private UUID bannerId;
  private String title;
  private String imageUrl;
  private String position;
  private Integer displayOrder;
  private Boolean isActive;
}
