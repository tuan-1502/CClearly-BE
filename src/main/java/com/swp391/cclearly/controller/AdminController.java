package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.admin.AdminUserResponse;
import com.swp391.cclearly.dto.admin.AuditLogPageResponse;
import com.swp391.cclearly.dto.admin.CreateUserRequest;
import com.swp391.cclearly.dto.admin.DashboardStatsResponse;
import com.swp391.cclearly.dto.admin.RevenueResponse;
import com.swp391.cclearly.dto.admin.SystemSettingResponse;
import com.swp391.cclearly.dto.admin.UpdateSettingsRequest;
import com.swp391.cclearly.dto.admin.UpdateUserRequest;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.service.AdminService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "APIs quản trị hệ thống")
public class AdminController {

  private final AdminService adminService;

  @Operation(summary = "Lấy thống kê dashboard")
  @GetMapping("/dashboard")
  public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
    return ResponseEntity.ok(adminService.getDashboardStats());
  }

  @Operation(summary = "Lấy danh sách người dùng")
  @GetMapping("/users")
  public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String role,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(adminService.getAllUsers(search, role, page, size));
  }

  @Operation(summary = "Cập nhật thông tin người dùng")
  @PatchMapping("/users/{userId}")
  public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
      @PathVariable UUID userId,
      @RequestBody UpdateUserRequest request) {
    return ResponseEntity.ok(adminService.updateUser(userId, request));
  }

  @Operation(summary = "Tạo tài khoản mới")
  @PostMapping("/users")
  public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(
      @Valid @RequestBody CreateUserRequest request) {
    return ResponseEntity.ok(adminService.createUser(request));
  }

  @Operation(summary = "Xóa (vô hiệu hóa) tài khoản")
  @DeleteMapping("/users/{userId}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
    return ResponseEntity.ok(adminService.deleteUser(userId));
  }

  @Operation(summary = "Lấy thống kê doanh thu")
  @GetMapping("/revenue")
  public ResponseEntity<ApiResponse<RevenueResponse>> getRevenue(
      @RequestParam(defaultValue = "7") int days) {
    return ResponseEntity.ok(adminService.getRevenue(days));
  }

  @Operation(summary = "Lấy cấu hình hệ thống")
  @GetMapping("/settings")
  public ResponseEntity<ApiResponse<List<SystemSettingResponse>>> getSettings() {
    return ResponseEntity.ok(adminService.getSettings());
  }

  @Operation(summary = "Cập nhật cấu hình hệ thống")
  @PatchMapping("/settings")
  public ResponseEntity<ApiResponse<List<SystemSettingResponse>>> updateSettings(
      @RequestBody UpdateSettingsRequest request) {
    return ResponseEntity.ok(adminService.updateSettings(request));
  }

  @Operation(summary = "Lấy nhật ký hệ thống")
  @GetMapping("/logs")
  public ResponseEntity<ApiResponse<AuditLogPageResponse>> getAuditLogs(
      @RequestParam(required = false) String action,
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(adminService.getAuditLogs(action, fromDate, toDate, page, size));
  }
}
