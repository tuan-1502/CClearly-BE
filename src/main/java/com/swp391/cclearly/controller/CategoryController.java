package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.product.ProductPageResponse;
import com.swp391.cclearly.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "APIs danh mục sản phẩm")
public class CategoryController {

  private final ProductService productService;

  @Operation(summary = "Lấy danh sách danh mục")
  @GetMapping
  public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategories() {
    return ResponseEntity.ok(productService.getCategories());
  }
}
