package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.promotion.CreatePromotionRequest;
import com.swp391.cclearly.dto.promotion.PromotionResponse;
import com.swp391.cclearly.entity.Promotion;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.PromotionRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionService {

  private final PromotionRepository promotionRepository;
  private final AuditLogService auditLogService;

  public ApiResponse<List<PromotionResponse>> getAllPromotions() {
    List<PromotionResponse> response = promotionRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    return ApiResponse.success("Lấy danh sách khuyến mãi thành công", response);
  }

  public ApiResponse<PromotionResponse> getPromotionById(UUID id) {
    Promotion promotion = promotionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi"));
    return ApiResponse.success("Lấy thông tin khuyến mãi thành công", toResponse(promotion));
  }

  @Transactional
  public ApiResponse<PromotionResponse> createPromotion(CreatePromotionRequest request) {
    if (promotionRepository.findByCode(request.getCode()).isPresent()) {
      throw new BadRequestException("Mã khuyến mãi đã tồn tại");
    }

    Promotion promotion = Promotion.builder()
        .code(request.getCode().toUpperCase())
        .discountType(request.getDiscountType())
        .value(request.getValue())
        .description(request.getDescription())
        .minOrder(request.getMinOrder())
        .maxDiscount(request.getMaxDiscount())
        .usageLimit(request.getUsageLimit())
        .isActive(request.getIsActive() != null ? request.getIsActive() : true)
        .build();

    promotion = promotionRepository.save(promotion);
    auditLogService.log("ADD_VOUCHER",
        "Tạo voucher " + promotion.getCode()
            + " giảm " + promotion.getValue() + (promotion.getDiscountType() != null && promotion.getDiscountType().toUpperCase().startsWith("PERCENT") ? "%" : "đ"));
    return ApiResponse.success("Tạo khuyến mãi thành công", toResponse(promotion));
  }

  @Transactional
  public ApiResponse<PromotionResponse> updatePromotion(UUID id, CreatePromotionRequest request) {
    Promotion promotion = promotionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi"));

    if (request.getCode() != null) promotion.setCode(request.getCode().toUpperCase());
    if (request.getDiscountType() != null) promotion.setDiscountType(request.getDiscountType());
    if (request.getValue() != null) promotion.setValue(request.getValue());
    if (request.getDescription() != null) promotion.setDescription(request.getDescription());
    if (request.getMinOrder() != null) promotion.setMinOrder(request.getMinOrder());
    if (request.getMaxDiscount() != null) promotion.setMaxDiscount(request.getMaxDiscount());
    if (request.getUsageLimit() != null) promotion.setUsageLimit(request.getUsageLimit());
    if (request.getIsActive() != null) promotion.setIsActive(request.getIsActive());

    promotionRepository.save(promotion);
    auditLogService.log("UPDATE_VOUCHER",
        "Cập nhật voucher: " + promotion.getCode());
    return ApiResponse.success("Cập nhật khuyến mãi thành công", toResponse(promotion));
  }

  @Transactional
  public ApiResponse<Void> deletePromotion(UUID id) {
    Promotion promotion = promotionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi"));
    promotionRepository.delete(promotion);
    auditLogService.log("DELETE_VOUCHER",
        "Xóa voucher: " + promotion.getCode());
    return ApiResponse.success("Xóa khuyến mãi thành công", null);
  }

  /**
   * Bật/tắt trạng thái khuyến mãi
   * Dùng cho: PromotionPage toggleCouponStatus
   */
  @Transactional
  public ApiResponse<PromotionResponse> toggleStatus(UUID id) {
    Promotion promotion = promotionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi"));
    promotion.setIsActive(!Boolean.TRUE.equals(promotion.getIsActive()));
    promotionRepository.save(promotion);
    return ApiResponse.success(
        promotion.getIsActive() ? "Kích hoạt khuyến mãi thành công" : "Vô hiệu hóa khuyến mãi thành công",
        toResponse(promotion));
  }

  private PromotionResponse toResponse(Promotion p) {
    return PromotionResponse.builder()
        .promotionId(p.getPromotionId())
        .code(p.getCode())
        .discountType(p.getDiscountType())
        .value(p.getValue())
        .description(p.getDescription())
        .minOrder(p.getMinOrder())
        .maxDiscount(p.getMaxDiscount())
        .usageLimit(p.getUsageLimit())
        .usageCount(p.getOrders() != null ? p.getOrders().size() : 0)
        .isActive(Boolean.TRUE.equals(p.getIsActive()))
        .build();
  }
}
