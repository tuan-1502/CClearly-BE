package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.banner.BannerResponse;
import com.swp391.cclearly.dto.banner.CreateBannerRequest;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.entity.ContentBanner;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.ContentBannerRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

  private final ContentBannerRepository bannerRepository;
  private final AuditLogService auditLogService;

  public ApiResponse<List<BannerResponse>> getAllBanners() {
    List<BannerResponse> response = bannerRepository.findAllByOrderByPositionAscDisplayOrderAsc()
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    return ApiResponse.success("Lấy danh sách banner thành công", response);
  }

  public ApiResponse<List<BannerResponse>> getActiveBanners() {
    List<BannerResponse> response = bannerRepository.findByIsActiveTrueOrderByPositionAscDisplayOrderAsc()
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    return ApiResponse.success("Lấy danh sách banner thành công", response);
  }

  public ApiResponse<List<BannerResponse>> getActiveBannersByPosition(String position) {
    List<BannerResponse> response = bannerRepository.findByPositionAndIsActiveTrueOrderByDisplayOrderAsc(position)
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    return ApiResponse.success("Lấy danh sách banner thành công", response);
  }

  public ApiResponse<BannerResponse> getBannerById(UUID id) {
    ContentBanner banner = bannerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy banner"));
    return ApiResponse.success("Lấy thông tin banner thành công", toResponse(banner));
  }

  @Transactional
  public ApiResponse<BannerResponse> createBanner(CreateBannerRequest request) {
    ContentBanner banner = ContentBanner.builder()
        .title(request.getTitle())
        .imageUrl(request.getImageUrl())
        .position(request.getPosition())
        .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 1)
        .isActive(request.getIsActive() != null ? request.getIsActive() : true)
        .build();

    banner = bannerRepository.save(banner);
    auditLogService.log("CHANGE_BANNER",
        "Tạo banner mới: " + banner.getTitle() + " (" + banner.getPosition() + ")");
    return ApiResponse.success("Tạo banner thành công", toResponse(banner));
  }

  @Transactional
  public ApiResponse<BannerResponse> updateBanner(UUID id, CreateBannerRequest request) {
    ContentBanner banner = bannerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy banner"));

    if (request.getTitle() != null) banner.setTitle(request.getTitle());
    if (request.getImageUrl() != null) banner.setImageUrl(request.getImageUrl());
    if (request.getPosition() != null) banner.setPosition(request.getPosition());
    if (request.getDisplayOrder() != null) banner.setDisplayOrder(request.getDisplayOrder());
    if (request.getIsActive() != null) banner.setIsActive(request.getIsActive());

    bannerRepository.save(banner);
    auditLogService.log("CHANGE_BANNER",
        "Cập nhật banner: " + banner.getTitle() + " (" + banner.getPosition() + ")");
    return ApiResponse.success("Cập nhật banner thành công", toResponse(banner));
  }

  @Transactional
  public ApiResponse<Void> deleteBanner(UUID id) {
    ContentBanner banner = bannerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy banner"));
    bannerRepository.delete(banner);
    auditLogService.log("CHANGE_BANNER",
        "Xóa banner: " + banner.getTitle() + " (" + banner.getPosition() + ")");
    return ApiResponse.success("Xóa banner thành công", null);
  }

  private BannerResponse toResponse(ContentBanner b) {
    return BannerResponse.builder()
        .bannerId(b.getBannerId())
        .title(b.getTitle())
        .imageUrl(b.getImageUrl())
        .position(b.getPosition())
        .displayOrder(b.getDisplayOrder())
        .isActive(b.getIsActive())
        .build();
  }
}
