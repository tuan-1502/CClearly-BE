package com.swp391.cclearly.service;

import com.swp391.cclearly.entity.Promotion;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.repository.OrderRepository;
import com.swp391.cclearly.repository.PromotionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionValidationService {

  private final PromotionRepository promotionRepository;
  private final OrderRepository orderRepository;

  public AppliedPromotion validate(String code, BigDecimal orderTotal) {
    String normalizedCode = code != null ? code.toUpperCase().trim() : "";
    BigDecimal safeOrderTotal = orderTotal != null ? orderTotal : BigDecimal.ZERO;

    if (normalizedCode.isEmpty()) {
      throw new BadRequestException("Vui lòng nhập mã giảm giá");
    }

    Promotion promo = promotionRepository.findByCode(normalizedCode)
        .orElseThrow(() -> new BadRequestException("Mã giảm giá không tồn tại"));

    if (!Boolean.TRUE.equals(promo.getIsActive())) {
      throw new BadRequestException("Mã giảm giá đã hết hiệu lực");
    }

    if (promo.getValue() == null || promo.getValue().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Giá trị giảm giá của mã này không hợp lệ (phải lớn hơn 0)");
    }

    if (promo.getUsageLimit() != null
        && orderRepository.countByCoupon_PromotionId(promo.getPromotionId()) >= promo.getUsageLimit()) {
      throw new BadRequestException("Mã giảm giá đã hết lượt sử dụng");
    }

    if (promo.getMinOrder() != null && safeOrderTotal.compareTo(promo.getMinOrder()) < 0) {
      throw new BadRequestException("Đơn hàng chưa đạt giá trị tối thiểu để dùng mã này");
    }

    boolean isPercent = isPercentDiscount(promo.getDiscountType());
    BigDecimal discountAmount;
    if (isPercent) {
      discountAmount = safeOrderTotal.multiply(promo.getValue())
          .divide(BigDecimal.valueOf(100), 0, RoundingMode.FLOOR);
      if (promo.getMaxDiscount() != null && discountAmount.compareTo(promo.getMaxDiscount()) > 0) {
        discountAmount = promo.getMaxDiscount();
      }
    } else if ("FIXED".equalsIgnoreCase(promo.getDiscountType())) {
      discountAmount = promo.getValue();
    } else {
      throw new BadRequestException("Loại mã giảm giá không hợp lệ");
    }

    if (discountAmount.compareTo(safeOrderTotal) > 0) {
      discountAmount = safeOrderTotal;
    }

    return new AppliedPromotion(promo, isPercent ? "PERCENTAGE" : "FIXED", discountAmount);
  }

  public Map<String, Object> toValidationResponse(AppliedPromotion appliedPromotion) {
    Promotion promo = appliedPromotion.promotion();

    Map<String, Object> result = new HashMap<>();
    result.put("code", promo.getCode());
    result.put("discountType", appliedPromotion.discountType());
    result.put("value", promo.getValue());
    result.put("discountAmount", appliedPromotion.discountAmount());
    result.put("maxDiscount", promo.getMaxDiscount());
    result.put("minOrder", promo.getMinOrder());
    result.put("description", promo.getDescription() != null ? promo.getDescription() : "");
    return result;
  }

  private boolean isPercentDiscount(String discountType) {
    return "PERCENT".equalsIgnoreCase(discountType)
        || "PERCENTAGE".equalsIgnoreCase(discountType);
  }

  public record AppliedPromotion(Promotion promotion, String discountType, BigDecimal discountAmount) {}
}
