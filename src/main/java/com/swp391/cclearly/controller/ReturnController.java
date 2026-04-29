package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.refund.RefundResponse;
import com.swp391.cclearly.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Returns", description = "APIs quản lý đổi trả / hoàn tiền")
public class ReturnController {

  private final RefundService refundService;

  @Operation(summary = "Lấy danh sách yêu cầu đổi trả")
  @GetMapping
  public ResponseEntity<ApiResponse<List<RefundResponse>>> getAllReturns(
      @RequestParam(required = false) String status) {
    return ResponseEntity.ok(refundService.getAllReturns(status));
  }

  @Operation(summary = "Lấy chi tiết yêu cầu đổi trả")
  @GetMapping("/{refundId}")
  public ResponseEntity<ApiResponse<RefundResponse>> getReturnById(
      @PathVariable UUID refundId) {
    return ResponseEntity.ok(refundService.getReturnById(refundId));
  }

  @Operation(summary = "Duyệt yêu cầu đổi trả")
  @PutMapping("/{refundId}/approve")
  public ResponseEntity<ApiResponse<RefundResponse>> approveReturn(
      @PathVariable UUID refundId) {
    return ResponseEntity.ok(refundService.approveReturn(refundId));
  }

  @Operation(summary = "Từ chối yêu cầu đổi trả")
  @PutMapping("/{refundId}/reject")
  public ResponseEntity<ApiResponse<RefundResponse>> rejectReturn(
      @PathVariable UUID refundId,
      @RequestBody(required = false) Map<String, String> body) {
    String reason = body != null ? body.get("reason") : null;
    return ResponseEntity.ok(refundService.rejectReturn(refundId, reason));
  }

  @Operation(summary = "Hoàn tất đổi trả")
  @PutMapping("/{refundId}/complete")
  public ResponseEntity<ApiResponse<RefundResponse>> completeReturn(
      @PathVariable UUID refundId) {
    return ResponseEntity.ok(refundService.completeReturn(refundId));
  }
}
