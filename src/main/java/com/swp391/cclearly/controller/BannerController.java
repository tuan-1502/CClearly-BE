package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.banner.BannerResponse;
import com.swp391.cclearly.dto.banner.CreateBannerRequest;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.service.BannerService;
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
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@Tag(name = "Banners", description = "APIs quản lý banner")
public class BannerController {

  private final BannerService bannerService;

  @Operation(summary = "Lấy danh sách banner đang hiện (public - cho homepage)")
  @GetMapping("/active")
  public ResponseEntity<ApiResponse<List<BannerResponse>>> getActiveBanners(
      @RequestParam(required = false) String position) {
    if (position != null && !position.isBlank()) {
      return ResponseEntity.ok(bannerService.getActiveBannersByPosition(position));
    }
    return ResponseEntity.ok(bannerService.getActiveBanners());
  }

  @Operation(summary = "Lấy tất cả banner (admin)")
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping
  public ResponseEntity<ApiResponse<List<BannerResponse>>> getAllBanners() {
    return ResponseEntity.ok(bannerService.getAllBanners());
  }

  @Operation(summary = "Lấy chi tiết banner")
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BannerResponse>> getBannerById(@PathVariable UUID id) {
    return ResponseEntity.ok(bannerService.getBannerById(id));
  }

  @Operation(summary = "Tạo banner mới")
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping
  public ResponseEntity<ApiResponse<BannerResponse>> createBanner(
      @Valid @RequestBody CreateBannerRequest request) {
    return ResponseEntity.ok(bannerService.createBanner(request));
  }

  @Operation(summary = "Cập nhật banner")
  @SecurityRequirement(name = "bearerAuth")
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
      @PathVariable UUID id,
      @RequestBody CreateBannerRequest request) {
    return ResponseEntity.ok(bannerService.updateBanner(id, request));
  }

  @Operation(summary = "Xóa banner")
  @SecurityRequirement(name = "bearerAuth")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable UUID id) {
    return ResponseEntity.ok(bannerService.deleteBanner(id));
  }
}
