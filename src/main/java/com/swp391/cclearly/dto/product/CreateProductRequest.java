package com.swp391.cclearly.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateProductRequest {

  @NotBlank(message = "Tên sản phẩm không được để trống")
  private String name;

  @NotBlank(message = "Loại sản phẩm không được để trống")
  private String type; // frame, lens, accessory

  @NotNull(message = "Giá cơ bản không được để trống")
  private BigDecimal price;

  private String subCategory;
  private String description;
  private List<String> imageUrls;

  // Frame-specific attributes
  private FrameAttributes frameAttributes;

  // Lens-specific attributes
  private LensAttributes lensAttributes;

  // Variants
  private List<VariantRequest> variants;

  @Data
  public static class FrameAttributes {
    private String material;
    private String shape;
    private Integer lensWidth;
    private Integer bridgeWidth;
    private Integer templeLength;
  }

  @Data
  public static class LensAttributes {
    private String material;
    private String type; // lens type: Đơn tròng, Đa tròng, etc.
    private List<String> technologies;
  }

  @Data
  public static class VariantRequest {
    private UUID variantId; // null for new, set for existing (update)
    private String sku;
    private String colorName;
    private Float refractiveIndex;
    private BigDecimal salePrice;
    private Boolean isPreorder;
    private List<String> images;
  }
}
