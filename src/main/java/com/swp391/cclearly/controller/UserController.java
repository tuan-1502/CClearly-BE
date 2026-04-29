package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.address.AddressRequest;
import com.swp391.cclearly.dto.address.AddressResponse;
import com.swp391.cclearly.dto.admin.AdminUserResponse;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.order.OrderResponse;
import com.swp391.cclearly.dto.order.ReturnRequest;
import com.swp391.cclearly.dto.user.UpdateProfileRequest;
import com.swp391.cclearly.dto.user.UserProfileResponse;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.service.AddressService;
import com.swp391.cclearly.service.OrderService;
import com.swp391.cclearly.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "APIs quản lý thông tin người dùng")
public class UserController {

  private final UserService userService;
  private final OrderService orderService;
  private final AddressService addressService;

  @Operation(summary = "Lấy danh sách khách hàng")
  @GetMapping("/customers")
  public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getCustomers() {
    return ResponseEntity.ok(userService.getCustomers());
  }

  @Operation(summary = "Lấy thông tin cá nhân")
  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(userService.getProfile(user));
  }

  @Operation(summary = "Cập nhật thông tin cá nhân")
  @PatchMapping("/profile")
  public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
      @AuthenticationPrincipal User user,
      @RequestBody UpdateProfileRequest request) {
    return ResponseEntity.ok(userService.updateProfile(user, request));
  }

  @Operation(summary = "Lấy danh sách đơn hàng của người dùng")
  @GetMapping("/orders")
  public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(orderService.getUserOrders(user));
  }

  @Operation(summary = "Yêu cầu trả hàng/hoàn tiền")
  @PostMapping("/orders/{orderId}/return")
  public ResponseEntity<ApiResponse<Void>> requestReturn(
      @AuthenticationPrincipal User user,
      @PathVariable UUID orderId,
      @Valid @RequestBody ReturnRequest request) {
    return ResponseEntity.ok(orderService.requestReturn(user, orderId, request));
  }

  // ── Address endpoints ──

  @Operation(summary = "Lấy danh sách địa chỉ")
  @GetMapping("/addresses")
  public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(addressService.getAddresses(user));
  }

  @Operation(summary = "Thêm địa chỉ mới")
  @PostMapping("/addresses")
  public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody AddressRequest request) {
    return ResponseEntity.ok(addressService.createAddress(user, request));
  }

  @Operation(summary = "Cập nhật địa chỉ")
  @PutMapping("/addresses/{addressId}")
  public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
      @AuthenticationPrincipal User user,
      @PathVariable UUID addressId,
      @Valid @RequestBody AddressRequest request) {
    return ResponseEntity.ok(addressService.updateAddress(user, addressId, request));
  }

  @Operation(summary = "Xóa địa chỉ")
  @DeleteMapping("/addresses/{addressId}")
  public ResponseEntity<ApiResponse<Void>> deleteAddress(
      @AuthenticationPrincipal User user,
      @PathVariable UUID addressId) {
    return ResponseEntity.ok(addressService.deleteAddress(user, addressId));
  }

  @Operation(summary = "Đặt địa chỉ mặc định")
  @PatchMapping("/addresses/{addressId}/default")
  public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
      @AuthenticationPrincipal User user,
      @PathVariable UUID addressId) {
    return ResponseEntity.ok(addressService.setDefault(user, addressId));
  }
}
