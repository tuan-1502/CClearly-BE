package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.admin.AdminUserResponse;
import com.swp391.cclearly.dto.admin.AuditLogPageResponse;
import com.swp391.cclearly.dto.admin.AuditLogResponse;
import com.swp391.cclearly.dto.admin.CreateUserRequest;
import com.swp391.cclearly.dto.admin.DashboardStatsResponse;
import com.swp391.cclearly.dto.admin.RevenueResponse;
import com.swp391.cclearly.dto.admin.SystemSettingResponse;
import com.swp391.cclearly.dto.admin.UpdateSettingsRequest;
import com.swp391.cclearly.dto.admin.UpdateUserRequest;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.entity.AuditLog;
import com.swp391.cclearly.entity.Order;
import com.swp391.cclearly.entity.SystemConfig;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.AuditLogRepository;
import com.swp391.cclearly.repository.OrderRepository;
import com.swp391.cclearly.repository.ProductRepository;
import com.swp391.cclearly.repository.RoleRepository;
import com.swp391.cclearly.repository.SystemConfigRepository;
import com.swp391.cclearly.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final SystemConfigRepository systemConfigRepository;
  private final RoleRepository roleRepository;
  private final AuditLogRepository auditLogRepository;
  private final AuditLogService auditLogService;
  private final PasswordEncoder passwordEncoder;

  public ApiResponse<DashboardStatsResponse> getDashboardStats() {
    long totalOrders = orderRepository.count();
    long totalCustomers = userRepository.count();
    long totalProducts = productRepository.count();

    long pendingOrders = orderRepository.countByStatus("PENDING");
    long processingOrders = orderRepository.countByStatus("PROCESSING");
    long deliveredOrders = orderRepository.countByStatus("DELIVERED");
    long cancelledOrders = orderRepository.countByStatus("CANCELLED");

    // Total revenue from delivered orders
    ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
    List<Order> allOrders = orderRepository.findAll();
    List<Order> deliveredOrderList = allOrders.stream()
        .filter(o -> "DELIVERED".equals(o.getStatus()))
        .collect(Collectors.toList());

    BigDecimal totalRevenue = deliveredOrderList.stream()
        .map(Order::getFinalAmount)
        .filter(a -> a != null)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Orders by status
    Map<String, Long> ordersByStatus = new HashMap<>();
    ordersByStatus.put("PENDING", pendingOrders);
    ordersByStatus.put("PROCESSING", processingOrders);
    ordersByStatus.put("DELIVERED", deliveredOrders);
    ordersByStatus.put("CANCELLED", cancelledOrders);

    // Revenue by month (last 6 months)
    YearMonth currentMonth = YearMonth.now(zoneId);
    List<DashboardStatsResponse.RevenueByMonth> revenueByMonth = new ArrayList<>();
    for (int i = 5; i >= 0; i--) {
      YearMonth ym = currentMonth.minusMonths(i);
      String monthLabel = String.valueOf(ym.getMonthValue());
      BigDecimal monthRevenue = BigDecimal.ZERO;
      long monthOrders = 0;
      for (Order o : deliveredOrderList) {
        if (o.getCreatedAt() != null) {
          YearMonth orderYm = YearMonth.from(o.getCreatedAt().atZone(zoneId));
          if (orderYm.equals(ym)) {
            monthRevenue = monthRevenue.add(
                o.getFinalAmount() != null ? o.getFinalAmount() : BigDecimal.ZERO);
            monthOrders++;
          }
        }
      }
      revenueByMonth.add(DashboardStatsResponse.RevenueByMonth.builder()
          .month(monthLabel)
          .revenue(monthRevenue)
          .orders(monthOrders)
          .build());
    }

    // Top products (from delivered order items)
    Map<UUID, String> productNames = new HashMap<>();
    Map<UUID, String> productTypes = new HashMap<>();
    Map<UUID, Long> productSold = new HashMap<>();
    Map<UUID, BigDecimal> productRevenue = new HashMap<>();
    for (Order o : deliveredOrderList) {
      if (o.getOrderItems() != null) {
        for (var item : o.getOrderItems()) {
          if (item.getVariant() != null && item.getVariant().getProduct() != null) {
            UUID productId = item.getVariant().getProduct().getProductId();
            productNames.putIfAbsent(productId, item.getVariant().getProduct().getName());
            productTypes.putIfAbsent(productId, item.getVariant().getProduct().getCategoryType());
            productSold.merge(productId, 1L, Long::sum);
            productRevenue.merge(productId,
                item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO,
                BigDecimal::add);
          }
        }
      }
    }
    List<DashboardStatsResponse.TopProduct> topProducts = productSold.entrySet().stream()
        .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
        .limit(5)
        .map(e -> DashboardStatsResponse.TopProduct.builder()
            .name(productNames.get(e.getKey()))
            .type(productTypes.get(e.getKey()))
            .sold(e.getValue())
            .revenue(productRevenue.get(e.getKey()))
            .build())
        .collect(Collectors.toList());

    DashboardStatsResponse stats = DashboardStatsResponse.builder()
        .totalOrders(totalOrders)
        .totalCustomers(totalCustomers)
        .totalProducts(totalProducts)
        .totalRevenue(totalRevenue)
        .pendingOrders(pendingOrders)
        .processingOrders(processingOrders)
        .deliveredOrders(deliveredOrders)
        .cancelledOrders(cancelledOrders)
        .ordersByStatus(ordersByStatus)
        .revenueByMonth(revenueByMonth)
        .topProducts(topProducts)
        .build();

    return ApiResponse.success("Lấy thống kê dashboard thành công", stats);
  }

  public ApiResponse<List<AdminUserResponse>> getAllUsers(
      String search, String role, int page, int size) {
    List<User> allUsers = userRepository.findAll();

    // Filter
    List<User> filtered = allUsers.stream()
        .filter(u -> {
          if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            return (u.getFullName() != null && u.getFullName().toLowerCase().contains(q))
                || (u.getEmail() != null && u.getEmail().toLowerCase().contains(q));
          }
          return true;
        })
        .filter(u -> {
          if (role != null && !role.isBlank()) {
            return u.getRole() != null && role.equalsIgnoreCase(u.getRole().getRoleName());
          }
          return true;
        })
        .collect(Collectors.toList());

    // Paginate manually
    int start = (page - 1) * size;
    int end = Math.min(start + size, filtered.size());
    List<AdminUserResponse> response = (start < filtered.size())
        ? filtered.subList(start, end).stream().map(this::toAdminUserResponse).collect(Collectors.toList())
        : new ArrayList<>();

    return ApiResponse.success("Lấy danh sách người dùng thành công", response);
  }

  @Transactional
  public ApiResponse<AdminUserResponse> updateUser(UUID userId, UpdateUserRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

    if (request.getFullName() != null) user.setFullName(request.getFullName());
    if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
    if (request.getStatus() != null) user.setStatus(request.getStatus());

    if (request.getRole() != null) {
      roleRepository.findByRoleName(request.getRole())
          .ifPresent(user::setRole);
    }

    userRepository.save(user);
    auditLogService.log("UPDATE_USER",
        "Cập nhật người dùng: " + user.getEmail());
    return ApiResponse.success("Cập nhật người dùng thành công", toAdminUserResponse(user));
  }

  public ApiResponse<RevenueResponse> getRevenue(int days) {
    ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
    List<Order> deliveredOrders = orderRepository.findAll().stream()
        .filter(o -> "DELIVERED".equals(o.getStatus()))
        .collect(Collectors.toList());

    BigDecimal totalRevenue = deliveredOrders.stream()
        .map(Order::getFinalAmount)
        .filter(a -> a != null)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // This month / Last month revenue
    YearMonth thisMonth = YearMonth.now(zoneId);
    YearMonth lastMonth = thisMonth.minusMonths(1);

    BigDecimal thisMonthRevenue = BigDecimal.ZERO;
    BigDecimal lastMonthRevenue = BigDecimal.ZERO;

    for (Order o : deliveredOrders) {
      if (o.getCreatedAt() != null && o.getFinalAmount() != null) {
        YearMonth orderYm = YearMonth.from(o.getCreatedAt().atZone(zoneId));
        if (orderYm.equals(thisMonth)) {
          thisMonthRevenue = thisMonthRevenue.add(o.getFinalAmount());
        } else if (orderYm.equals(lastMonth)) {
          lastMonthRevenue = lastMonthRevenue.add(o.getFinalAmount());
        }
      }
    }

    double growthPercent = 0.0;
    if (lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
      growthPercent = thisMonthRevenue.subtract(lastMonthRevenue)
          .divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
          .doubleValue() * 100;
    }

    // Revenue by day (last N days)
    int numDays = Math.max(1, Math.min(days, 365));
    LocalDate today = LocalDate.now(zoneId);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    List<RevenueResponse.DailyRevenue> revenueByDay = new ArrayList<>();
    for (int i = numDays - 1; i >= 0; i--) {
      LocalDate day = today.minusDays(i);
      BigDecimal dayRevenue = BigDecimal.ZERO;
      long dayOrders = 0;
      for (Order o : deliveredOrders) {
        if (o.getCreatedAt() != null) {
          LocalDate orderDate = o.getCreatedAt().atZone(zoneId).toLocalDate();
          if (orderDate.equals(day)) {
            dayRevenue = dayRevenue.add(
                o.getFinalAmount() != null ? o.getFinalAmount() : BigDecimal.ZERO);
            dayOrders++;
          }
        }
      }
      revenueByDay.add(RevenueResponse.DailyRevenue.builder()
          .date(day.format(dateFormatter))
          .revenue(dayRevenue)
          .orders(dayOrders)
          .build());
    }

    RevenueResponse response = RevenueResponse.builder()
        .totalRevenue(totalRevenue)
        .thisMonthRevenue(thisMonthRevenue)
        .lastMonthRevenue(lastMonthRevenue)
        .growthPercent(growthPercent)
        .revenueByDay(revenueByDay)
        .build();

    return ApiResponse.success("Lấy doanh thu thành công", response);
  }

  public ApiResponse<List<SystemSettingResponse>> getSettings() {
    List<SystemConfig> configs = systemConfigRepository.findAll();
    List<SystemSettingResponse> response = configs.stream()
        .map(c -> SystemSettingResponse.builder()
            .key(c.getConfigKey())
            .value(c.getConfigValue())
            .group(c.getConfigGroup())
            .build())
        .collect(Collectors.toList());
    return ApiResponse.success("Lấy cấu hình hệ thống thành công", response);
  }

  @Transactional
  public ApiResponse<List<SystemSettingResponse>> updateSettings(UpdateSettingsRequest request) {
    if (request.getSettings() != null) {
      for (var entry : request.getSettings().entrySet()) {
        SystemConfig config = systemConfigRepository.findByConfigKey(entry.getKey())
            .orElse(SystemConfig.builder()
                .configKey(entry.getKey())
                .configGroup("general")
                .build());
        config.setConfigValue(entry.getValue());
        systemConfigRepository.save(config);
      }
    }
    auditLogService.log("UPDATE_SETTINGS",
        "Cập nhật cấu hình hệ thống: " + request.getSettings().keySet());
    return getSettings();
  }

  /**
   * Tạo tài khoản người dùng mới (nhân viên hoặc khách hàng)
   * Dùng cho: StaffPage "Thêm nhân viên", RolePermissionPage "Thêm nhân sự mới"
   */
  @Transactional
  public ApiResponse<AdminUserResponse> createUser(CreateUserRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email đã được sử dụng");
    }

    if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()
        && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
      throw new BadRequestException("Số điện thoại đã được sử dụng");
    }

    var role = roleRepository.findByRoleName(request.getRole().toUpperCase())
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò: " + request.getRole()));

    User user = User.builder()
        .email(request.getEmail())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .fullName(request.getFullName())
        .phoneNumber(request.getPhoneNumber())
        .role(role)
        .status("ACTIVE")
        .isEmailVerified(true) // Admin creates => auto verified
        .createdAt(Instant.now())
        .build();

    user = userRepository.save(user);
    auditLogService.log("CREATE_USER",
        "Tạo tài khoản mới cho nhân viên: " + user.getEmail());
    return ApiResponse.success("Tạo tài khoản thành công", toAdminUserResponse(user));
  }

  /**
   * Xóa (vô hiệu hóa) tài khoản người dùng
   * Soft delete: set status = INACTIVE
   * Dùng cho: StaffPage "Xóa", RolePermissionPage
   */
  @Transactional
  public ApiResponse<Void> deleteUser(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

    user.setStatus("INACTIVE");
    userRepository.save(user);
    auditLogService.log("BAN_ACCOUNT",
        "Khóa tài khoản " + user.getEmail());

    return ApiResponse.success("Đã vô hiệu hóa tài khoản", null);
  }

  /**
   * Lấy danh sách nhật ký hệ thống (audit logs)
   * Dùng cho: SystemLogsPage, RolePermissionPage "Audit Log" tab
   */
  public ApiResponse<AuditLogPageResponse> getAuditLogs(
      String action, LocalDate fromDate, LocalDate toDate, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<AuditLog> logPage;

    Instant from = (fromDate != null)
        ? fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        : null;
    Instant to = (toDate != null)
        ? toDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()
        : null;

    boolean hasAction = action != null && !action.isBlank();
    boolean hasDateRange = from != null && to != null;

    if (hasAction && hasDateRange) {
      logPage = auditLogRepository.findByActionAndCreatedAtBetweenOrderByCreatedAtDesc(
          action, from, to, pageable);
    } else if (hasAction) {
      logPage = auditLogRepository.findByActionOrderByCreatedAtDesc(action, pageable);
    } else if (hasDateRange) {
      logPage = auditLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(
          from, to, pageable);
    } else {
      logPage = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    List<AuditLogResponse> items = logPage.getContent().stream()
        .map(log -> AuditLogResponse.builder()
            .logId(log.getLogId())
            .userId(log.getUser() != null ? log.getUser().getUserId().toString() : null)
            .userName(log.getUser() != null ? log.getUser().getFullName() : "System")
            .action(log.getAction())
            .details(log.getDetails())
            .oldValue(log.getOldValue())
            .newValue(log.getNewValue())
            .ipAddress(log.getIpAddress())
            .createdAt(log.getCreatedAt())
            .build())
        .collect(Collectors.toList());

    AuditLogPageResponse response = AuditLogPageResponse.builder()
        .items(items)
        .meta(AuditLogPageResponse.Meta.builder()
            .page(page)
            .size(size)
            .totalElements(logPage.getTotalElements())
            .totalPages(logPage.getTotalPages())
            .build())
        .build();

    return ApiResponse.success("Lấy nhật ký hệ thống thành công", response);
  }

  private AdminUserResponse toAdminUserResponse(User user) {
    return AdminUserResponse.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .phoneNumber(user.getPhoneNumber())
        .role(user.getRole() != null ? user.getRole().getRoleName() : null)
        .status(user.getStatus())
        .isEmailVerified(user.getIsEmailVerified())
        .createdAt(user.getCreatedAt())
        .lastLogin(user.getLastLogin())
        .build();
  }
}
