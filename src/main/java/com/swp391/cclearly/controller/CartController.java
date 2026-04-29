package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.cart.AddCartItemRequest;
import com.swp391.cclearly.dto.cart.CartResponse;
import com.swp391.cclearly.dto.cart.UpdateCartItemRequest;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cart", description = "APIs quản lý giỏ hàng")
public class CartController {

  private final CartService cartService;

  @Operation(summary = "Lấy giỏ hàng hiện tại")
  @GetMapping
  public ResponseEntity<ApiResponse<CartResponse>> getCart(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(cartService.getCart(user));
  }

  @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
  @PostMapping("/items")
  public ResponseEntity<ApiResponse<CartResponse>> addToCart(
      @AuthenticationPrincipal User user,
      @Valid @RequestBody AddCartItemRequest request) {
    return ResponseEntity.ok(cartService.addToCart(user, request));
  }

  @Operation(summary = "Cập nhật số lượng sản phẩm trong giỏ")
  @PatchMapping("/items/{cartItemId}")
  public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
      @AuthenticationPrincipal User user,
      @PathVariable UUID cartItemId,
      @Valid @RequestBody UpdateCartItemRequest request) {
    return ResponseEntity.ok(cartService.updateCartItem(user, cartItemId, request));
  }

  @Operation(summary = "Xóa sản phẩm khỏi giỏ")
  @DeleteMapping("/items/{cartItemId}")
  public ResponseEntity<ApiResponse<Void>> removeCartItem(
      @AuthenticationPrincipal User user,
      @PathVariable UUID cartItemId) {
    return ResponseEntity.ok(cartService.removeCartItem(user, cartItemId));
  }

  @Operation(summary = "Xóa toàn bộ giỏ hàng")
  @DeleteMapping("/clear")
  public ResponseEntity<ApiResponse<Void>> clearCart(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(cartService.clearCart(user));
  }
}
