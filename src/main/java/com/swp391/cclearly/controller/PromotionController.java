package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.promotion.CreatePromotionRequest;
import com.swp391.cclearly.dto.promotion.PromotionResponse;
import com.swp391.cclearly.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Promotions", description = "APIs quản lý khuyến mãi")
public class PromotionController {

  private final PromotionService promotionService;

  @Operation(summary = "Lấy danh sách khuyến mãi")
  @GetMapping
  public ResponseEntity<ApiResponse<List<PromotionResponse>>> getAllPromotions() {
    return ResponseEntity.ok(promotionService.getAllPromotions());
  }

  @Operation(summary = "Lấy chi tiết khuyến mãi")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PromotionResponse>> getPromotionById(@PathVariable UUID id) {
    return ResponseEntity.ok(promotionService.getPromotionById(id));
  }

  @Operation(summary = "Tạo khuyến mãi mới")
  @PostMapping
  public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(
      @Valid @RequestBody CreatePromotionRequest request) {
    return ResponseEntity.ok(promotionService.createPromotion(request));
  }

  @Operation(summary = "Cập nhật khuyến mãi")
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<PromotionResponse>> updatePromotion(
      @PathVariable UUID id,
      @RequestBody CreatePromotionRequest request) {
    return ResponseEntity.ok(promotionService.updatePromotion(id, request));
  }

  @Operation(summary = "Xóa khuyến mãi")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deletePromotion(@PathVariable UUID id) {
    return ResponseEntity.ok(promotionService.deletePromotion(id));
  }

  @Operation(summary = "Bật/tắt trạng thái khuyến mãi")
  @PatchMapping("/{id}/toggle")
  public ResponseEntity<ApiResponse<PromotionResponse>> toggleStatus(@PathVariable UUID id) {
    return ResponseEntity.ok(promotionService.toggleStatus(id));
  }
}
