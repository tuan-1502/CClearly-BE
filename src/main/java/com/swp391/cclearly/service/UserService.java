package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.admin.AdminUserResponse;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.user.UpdateProfileRequest;
import com.swp391.cclearly.dto.user.UserProfileResponse;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public ApiResponse<UserProfileResponse> getProfile(User user) {
    User managed = userRepository.findByIdWithRole(user.getUserId())
        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    return ApiResponse.success("Lấy thông tin thành công", toResponse(managed));
  }

  public ApiResponse<UserProfileResponse> updateProfile(User user, UpdateProfileRequest request) {
    User managed = userRepository.findByIdWithRole(user.getUserId())
        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      managed.setFullName(request.getFullName());
    }
    if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
      managed.setPhoneNumber(request.getPhoneNumber());
    }
    userRepository.save(managed);
    return ApiResponse.success("Cập nhật thông tin thành công", toResponse(managed));
  }

  @Transactional(readOnly = true)
  public ApiResponse<List<AdminUserResponse>> getCustomers() {
    List<AdminUserResponse> customers = userRepository.findByRole_RoleName("CUSTOMER")
        .stream()
        .map(this::toAdminUserResponse)
        .collect(Collectors.toList());
    return ApiResponse.success("Lấy danh sách khách hàng thành công", customers);
  }

  private UserProfileResponse toResponse(User user) {
    return UserProfileResponse.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .phoneNumber(user.getPhoneNumber())
        .isEmailVerified(user.getIsEmailVerified())
        .role(user.getRole().getRoleName())
        .build();
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
