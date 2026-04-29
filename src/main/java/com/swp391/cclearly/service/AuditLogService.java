package com.swp391.cclearly.service;

import com.swp391.cclearly.entity.AuditLog;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service trung tâm để ghi nhật ký hoạt động hệ thống.
 * Inject service này vào bất kỳ service nào cần ghi audit log.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Ghi audit log với đầy đủ thông tin.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String details, String oldValue, String newValue) {
        try {
            User currentUser = getCurrentUser();
            String ipAddress = getClientIpAddress();

            AuditLog auditLog = AuditLog.builder()
                    .user(currentUser)
                    .action(action)
                    .details(details)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .ipAddress(ipAddress)
                    .createdAt(Instant.now())
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Không để lỗi ghi log ảnh hưởng tới nghiệp vụ chính
            log.warn("Không thể ghi audit log: action={}, details={}, error={}", action, details, e.getMessage());
        }
    }

    /**
     * Ghi audit log đơn giản (không có giá trị cũ/mới).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String details) {
        log(action, details, null, null);
    }

    /**
     * Ghi audit log với User chỉ định (dùng khi chưa có SecurityContext, ví dụ đăng nhập).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(User user, String action, String details) {
        try {
            String ipAddress = getClientIpAddress();

            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .details(details)
                    .ipAddress(ipAddress)
                    .createdAt(Instant.now())
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("Không thể ghi audit log: action={}, details={}, error={}", action, details, e.getMessage());
        }
    }

    /**
     * Lấy User hiện tại từ SecurityContext.
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        return null;
    }

    /**
     * Lấy IP address của client từ HttpServletRequest.
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;

            HttpServletRequest request = attrs.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // X-Forwarded-For có thể chứa nhiều IP, lấy IP đầu tiên
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip;
        } catch (Exception e) {
            return null;
        }
    }
}
