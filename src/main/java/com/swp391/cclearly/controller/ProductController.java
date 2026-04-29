package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.product.CreateProductRequest;
import com.swp391.cclearly.dto.product.ProductPageResponse;
import com.swp391.cclearly.dto.product.ProductResponse;
import com.swp391.cclearly.dto.product.UpdateProductRequest;
import com.swp391.cclearly.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "APIs quản lý sản phẩm")
public class ProductController {

  private final ProductService productService;

  @Operation(summary = "Lấy danh sách sản phẩm", description = "Hỗ trợ lọc theo type, tìm kiếm, và phân trang")
  @GetMapping
  public ResponseEntity<ApiResponse<ProductPageResponse>> getProducts(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "12") int size) {
    return ResponseEntity.ok(productService.getProducts(type, search, page, size));
  }

  @Operation(summary = "Lấy danh sách gọng kính")
  @GetMapping("/frames")
  public ResponseEntity<ApiResponse<ProductPageResponse>> getFrames(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "12") int size) {
    return ResponseEntity.ok(productService.getProducts("frame", search, page, size));
  }

  @Operation(summary = "Lấy danh sách tròng kính")
  @GetMapping("/lenses")
  public ResponseEntity<ApiResponse<ProductPageResponse>> getLenses(
      @RequestParam(required = false) String search,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "12") int size) {
    return ResponseEntity.ok(productService.getProducts("lens", search, page, size));
  }

  @Operation(summary = "Lấy chi tiết sản phẩm")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @Operation(summary = "Tạo sản phẩm mới")
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping
  public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
      @Valid @RequestBody CreateProductRequest request) {
    return ResponseEntity.ok(productService.createProduct(request));
  }

  @Operation(summary = "Cập nhật sản phẩm")
  @SecurityRequirement(name = "bearerAuth")
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
      @PathVariable UUID id,
      @RequestBody UpdateProductRequest request) {
    return ResponseEntity.ok(productService.updateProduct(id, request));
  }

  @Operation(summary = "Xóa sản phẩm (soft delete)")
  @SecurityRequirement(name = "bearerAuth")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
    return ResponseEntity.ok(productService.deleteProduct(id));
  }
}
