package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.refund.RefundResponse;
import com.swp391.cclearly.entity.Order;
import com.swp391.cclearly.entity.OrderItem;
import com.swp391.cclearly.entity.ProductVariant;
import com.swp391.cclearly.entity.Refund;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.OrderRepository;
import com.swp391.cclearly.repository.RefundRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundService {

  private final RefundRepository refundRepository;
  private final OrderRepository orderRepository;

  /**
   * Lấy tất cả yêu cầu trả hàng/hoàn tiền
   * Có hỗ trợ filter theo status
   */
  public ApiResponse<List<RefundResponse>> getAllReturns(String status) {
    List<Refund> refunds;
    if (status != null && !status.isBlank()) {
      refunds = refundRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase());
    } else {
      refunds = refundRepository.findAllByOrderByCreatedAtDesc();
    }

    List<RefundResponse> response = refunds.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());

    return ApiResponse.success("Lấy danh sách đổi trả thành công", response);
  }

  /**
   * Lấy chi tiết một yêu cầu đổi trả
   */
  public ApiResponse<RefundResponse> getReturnById(UUID refundId) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đổi trả"));
    return ApiResponse.success("Lấy thông tin đổi trả thành công", toResponse(refund));
  }

  /**
   * Duyệt yêu cầu đổi trả (Sales/Operations)
   */
  @Transactional
  public ApiResponse<RefundResponse> approveReturn(UUID refundId) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đổi trả"));

    if (!"PENDING".equals(refund.getStatus())) {
      throw new BadRequestException("Chỉ có thể duyệt yêu cầu đang chờ xử lý");
    }

    refund.setStatus("APPROVED");
    refundRepository.save(refund);

    return ApiResponse.success("Đã duyệt yêu cầu đổi trả", toResponse(refund));
  }

  /**
   * Từ chối yêu cầu đổi trả
   */
  @Transactional
  public ApiResponse<RefundResponse> rejectReturn(UUID refundId, String rejectReason) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đổi trả"));

    if (!"PENDING".equals(refund.getStatus())) {
      throw new BadRequestException("Chỉ có thể từ chối yêu cầu đang chờ xử lý");
    }

    refund.setStatus("REJECTED");
    if (rejectReason != null && !rejectReason.isBlank()) {
      refund.setReason(refund.getReason() + " | Lý do từ chối: " + rejectReason);
    }
    refundRepository.save(refund);

    // Update order status back to DELIVERED
    Order order = refund.getOrder();
    order.setStatus("DELIVERED");
    orderRepository.save(order);

    return ApiResponse.success("Đã từ chối yêu cầu đổi trả", toResponse(refund));
  }

  /**
   * Hoàn tất đổi trả (đã nhận hàng trả + đã hoàn tiền)
   */
  @Transactional
  public ApiResponse<RefundResponse> completeReturn(UUID refundId) {
    Refund refund = refundRepository.findById(refundId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu đổi trả"));

    if (!"APPROVED".equals(refund.getStatus())) {
      throw new BadRequestException("Chỉ có thể hoàn tất yêu cầu đã được duyệt");
    }

    refund.setStatus("COMPLETED");
    refundRepository.save(refund);

    // Update order status
    Order order = refund.getOrder();
    order.setStatus("RETURNED");
    orderRepository.save(order);

    return ApiResponse.success("Đã hoàn tất đổi trả", toResponse(refund));
  }

  private RefundResponse toResponse(Refund r) {
    Order order = r.getOrder();

    // Build items from order items
    List<RefundResponse.RefundItemResponse> items = new ArrayList<>();
    if (order != null && order.getOrderItems() != null) {
      items = order.getOrderItems().stream()
          .map(oi -> {
            ProductVariant v = oi.getVariant();
            return RefundResponse.RefundItemResponse.builder()
                .name(v != null && v.getProduct() != null ? v.getProduct().getName() : "Sản phẩm")
                .quantity(1)
                .price(oi.getUnitPrice() != null ? oi.getUnitPrice() : BigDecimal.ZERO)
                .build();
          })
          .collect(Collectors.toList());
    }

    return RefundResponse.builder()
        .refundId(r.getRefundId())
        .orderId(order != null ? order.getOrderId() : null)
        .orderCode(order != null ? order.getCode() : null)
        .customerName(order != null && order.getUser() != null ? order.getUser().getFullName() : null)
        .customerPhone(order != null && order.getUser() != null ? order.getUser().getPhoneNumber() : null)
        .customerEmail(order != null && order.getUser() != null ? order.getUser().getEmail() : null)
        .status(r.getStatus())
        .type(r.getAmount() != null && r.getAmount().compareTo(BigDecimal.ZERO) > 0 ? "refund" : "return")
        .refundAmount(r.getAmount())
        .reason(r.getReason())
        .requestDate(r.getCreatedAt())
        .processedDate(!"PENDING".equals(r.getStatus()) ? Instant.now() : null)
        .items(items)
        .build();
  }
}
