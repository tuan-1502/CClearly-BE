package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.order.CreateOrderRequest;
import com.swp391.cclearly.dto.order.OrderPageResponse;
import com.swp391.cclearly.dto.order.OrderResponse;
import com.swp391.cclearly.dto.prescription.SavePrescriptionRequest;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders", description = "APIs quản lý đơn hàng")
public class OrderController {

  private final OrderService orderService;

  @Operation(summary = "Lấy danh sách đơn hàng của người dùng")
  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(orderService.getUserOrders(user));
  }

  @Operation(summary = "Lấy chi tiết đơn hàng")
  @GetMapping("/{orderId}")
  public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
      @AuthenticationPrincipal User user,
      @PathVariable UUID orderId) {
    return ResponseEntity.ok(orderService.getOrderById(user, orderId));
  }

  @Operation(summary = "Tạo đơn hàng mới từ giỏ hàng")
  @PostMapping
  public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
      @AuthenticationPrincipal User user,
      @RequestBody CreateOrderRequest request) {
    return ResponseEntity.ok(orderService.createOrder(user, request));
  }

  @Operation(summary = "Hủy đơn hàng")
  @PostMapping("/{orderId}/cancel")
  public ResponseEntity<ApiResponse<Void>> cancelOrder(
      @AuthenticationPrincipal User user,
      @PathVariable UUID orderId) {
    return ResponseEntity.ok(orderService.cancelOrder(user, orderId));
  }

  @Operation(summary = "Cập nhật trạng thái đơn hàng (Staff only)")
  @PatchMapping("/{orderId}/status")
  public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
      @PathVariable UUID orderId,
      @RequestBody Map<String, String> body) {
    String note = body.get("note");
    if (note != null) {
      return ResponseEntity.ok(orderService.updateOrderStatusWithNote(orderId, body.get("status"), note));
    }
    return ResponseEntity.ok(orderService.updateOrderStatus(orderId, body.get("status")));
  }

  @Operation(summary = "Lưu thông tin đơn kính (prescription) cho đơn hàng")
  @PutMapping("/{orderId}/prescription")
  public ResponseEntity<ApiResponse<Void>> savePrescription(
      @PathVariable UUID orderId,
      @RequestBody SavePrescriptionRequest request) {
    return ResponseEntity.ok(orderService.savePrescription(orderId, request));
  }

  @Operation(summary = "Lấy tất cả đơn hàng (Admin/Staff)")
  @GetMapping("/admin/all")
  public ResponseEntity<ApiResponse<OrderPageResponse>> getAllOrders(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(orderService.getAllOrders(status, page, size));
  }
}
