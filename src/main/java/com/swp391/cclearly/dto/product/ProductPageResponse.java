package com.swp391.cclearly.dto.product;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductPageResponse {
  private List<ProductResponse> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
}
