package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.repository.SystemConfigRepository;
import com.swp391.cclearly.service.PromotionValidationService;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

  private final SystemConfigRepository systemConfigRepository;
  private final PromotionValidationService promotionValidationService;

  @GetMapping("/maintenance-status")
  public ApiResponse<Map<String, Object>> getMaintenanceStatus() {
    boolean maintenance = systemConfigRepository.findByConfigKey("maintenance_mode")
        .map(c -> "true".equalsIgnoreCase(c.getConfigValue()))
        .orElse(false);

    return ApiResponse.success("OK", Map.of("maintenance", maintenance));
  }

  @GetMapping("/shipping-config")
  public ApiResponse<Map<String, Object>> getShippingConfig() {
    BigDecimal defaultShippingFee = systemConfigRepository.findByConfigKey("default_shipping_fee")
        .map(c -> new BigDecimal(c.getConfigValue()))
        .orElse(new BigDecimal("30000"));

    BigDecimal freeShippingThreshold = systemConfigRepository.findByConfigKey("free_shipping_threshold")
        .map(c -> new BigDecimal(c.getConfigValue()))
        .orElse(new BigDecimal("500000"));

    Map<String, Object> config = Map.of(
        "defaultShippingFee", defaultShippingFee,
        "freeShippingThreshold", freeShippingThreshold
    );

    return ApiResponse.success("Lấy cấu hình vận chuyển thành công", config);
  }

  @PostMapping("/promotions/validate")
  public ApiResponse<Map<String, Object>> validateVoucher(@RequestBody Map<String, Object> body) {
    Object rawCode = body.get("code");
    String code = rawCode != null ? rawCode.toString().toUpperCase().trim() : "";

    BigDecimal orderTotal;
    try {
      orderTotal = body.get("orderTotal") != null
          ? new BigDecimal(body.get("orderTotal").toString())
          : BigDecimal.ZERO;
    } catch (NumberFormatException ex) {
      return ApiResponse.error("Giá trị đơn hàng không hợp lệ");
    }

    if (code.isEmpty()) {
      return ApiResponse.error("Vui lòng nhập mã giảm giá");
    }

    try {
      var appliedPromotion = promotionValidationService.validate(code, orderTotal);
      return ApiResponse.success(
          "Áp dụng mã giảm giá thành công",
          promotionValidationService.toValidationResponse(appliedPromotion));
    } catch (BadRequestException ex) {
      return ApiResponse.error(ex.getMessage());
    }
  }
}
