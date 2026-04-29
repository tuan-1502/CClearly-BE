package com.swp391.cclearly.dto.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
  private UUID id;
  private String name;
  private String type; // frame, lens, accessory
  private String subCategory;
  private String sku;
  private BigDecimal basePrice;
  private Boolean isActive;
  private Boolean isSale;
  private BigDecimal salePrice;
  private List<String> images;
  private String description;
  private FrameInfo frame;
  private LensInfo lens;
  private List<VariantInfo> variants;

  @Data
  @Builder
  public static class FrameInfo {
    private String material;
    private String shape;
    private Integer lensWidthMm;
    private Integer bridgeWidthMm;
    private Integer templeLengthMm;
  }

  @Data
  @Builder
  public static class LensInfo {
    private String lensType;
    private String material;
    private List<String> technologies;
  }

  @Data
  @Builder
  public static class VariantInfo {
    private UUID variantId;
    private String sku;
    private String colorName;
    private Float refractiveIndex;
    private BigDecimal salePrice;
    private Boolean isPreorder;
    private List<String> images;
  }
}
