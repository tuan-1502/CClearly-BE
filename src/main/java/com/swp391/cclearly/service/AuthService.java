package com.swp391.cclearly.service;

import com.swp391.cclearly.config.JwtService;
import com.swp391.cclearly.dto.auth.*;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.entity.*;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final EmailVerificationRepository emailVerificationRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final EmailService emailService;
  private final AuditLogService auditLogService;

  @Value("${app.otp.expiration-minutes:5}")
  private int otpExpirationMinutes;

  @Value("${app.password-reset.expiration-minutes:30}")
  private int passwordResetExpirationMinutes;

  private static final String DEFAULT_ROLE = "CUSTOMER";
  private static final String USER_STATUS_ACTIVE = "ACTIVE";
  private static final String USER_STATUS_PENDING = "PENDING_VERIFICATION";

  /**
   * Register a new user
   */
  @Transactional
  public ApiResponse<AuthResponse> register(RegisterRequest request) {
    // Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email đã được sử dụng");
    }

    // Check if phone number already exists
    if (request.getPhoneNumber() != null
        && !request.getPhoneNumber().isEmpty()
        && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
      throw new BadRequestException("Số điện thoại đã được sử dụng");
    }

    // Get customer role
    Role customerRole =
        roleRepository
            .findByRoleName(DEFAULT_ROLE)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role CUSTOMER"));

    // Create new user
    User user =
        User.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .phoneNumber(request.getPhoneNumber())
            .role(customerRole)
            .status(USER_STATUS_PENDING)
            .isEmailVerified(false)
            .createdAt(Instant.now())
            .build();

    user = userRepository.save(user);

    // Generate OTP for email verification
    String otpCode = generateOtp();
    createEmailVerification(user, otpCode);

    // Send OTP email
    emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);

    // Generate token
    AuthResponse authResponse = generateAuthResponse(user);

    return ApiResponse.success("Đăng ký thành công. Vui lòng xác thực email.", authResponse);
  }

  /**
   * Login with email and password
   */
  @Transactional
  public ApiResponse<AuthResponse> login(LoginRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (BadCredentialsException e) {
      throw new BadRequestException("Email hoặc mật khẩu không đúng");
    }

    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("Email hoặc mật khẩu không đúng"));

    // Check user status
    if (!"ACTIVE".equals(user.getStatus()) && !"PENDING_VERIFICATION".equals(user.getStatus())) {
      throw new BadRequestException("Tài khoản đã bị khóa hoặc vô hiệu hóa");
    }

    // Update last login
    user.setLastLogin(Instant.now());
    userRepository.save(user);

    // Generate token
    AuthResponse authResponse = generateAuthResponse(user);

    auditLogService.log(user, "LOGIN",
        "Đăng nhập thành công: " + user.getEmail());
    return ApiResponse.success("Đăng nhập thành công", authResponse);
  }

  /**
   * Verify email with OTP
   */
  @Transactional
  public ApiResponse<Void> verifyEmail(VerifyEmailRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

    if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
      throw new BadRequestException("Email đã được xác thực trước đó");
    }

    EmailVerification verification =
        emailVerificationRepository
            .findByUserAndOtpCodeAndVerifiedFalse(user, request.getOtpCode())
            .orElseThrow(() -> new BadRequestException("Mã OTP không đúng"));

    // Check if OTP is expired
    if (verification.getExpiredAt().isBefore(Instant.now())) {
      throw new BadRequestException("Mã OTP đã hết hạn");
    }

    // Mark verification as verified
    verification.setVerified(true);
    emailVerificationRepository.save(verification);

    // Update user
    user.setIsEmailVerified(true);
    user.setStatus(USER_STATUS_ACTIVE);
    userRepository.save(user);

    return ApiResponse.success("Xác thực email thành công");
  }

  /**
   * Resend OTP for email verification
   */
  @Transactional
  public ApiResponse<Void> resendOtp(ResendOtpRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

    if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
      throw new BadRequestException("Email đã được xác thực trước đó");
    }

    // Generate new OTP
    String otpCode = generateOtp();
    createEmailVerification(user, otpCode);

    // Send OTP email
    emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);

    return ApiResponse.success("Đã gửi lại mã OTP. Vui lòng kiểm tra email.");
  }

  /**
   * Request password reset
   */
  @Transactional
  public ApiResponse<Void> forgotPassword(ForgotPasswordRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email này"));

    // Generate reset token
    String token = UUID.randomUUID().toString();

    PasswordResetToken resetToken =
        PasswordResetToken.builder()
            .user(user)
            .token(token)
            .expiredAt(Instant.now().plus(passwordResetExpirationMinutes, ChronoUnit.MINUTES))
            .used(false)
            .createdAt(Instant.now())
            .build();

    passwordResetTokenRepository.save(resetToken);

    // Send password reset email
    emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);

    return ApiResponse.success("Đã gửi link đặt lại mật khẩu đến email của bạn");
  }

  /**
   * Reset password with token
   */
  @Transactional
  public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
    PasswordResetToken resetToken =
        passwordResetTokenRepository
            .findByTokenAndUsedFalse(request.getToken())
            .orElseThrow(() -> new BadRequestException("Token không hợp lệ hoặc đã được sử dụng"));

    if (!resetToken.isValid()) {
      throw new BadRequestException("Token đã hết hạn");
    }

    // Update password
    User user = resetToken.getUser();
    user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    // Mark token as used
    resetToken.setUsed(true);
    passwordResetTokenRepository.save(resetToken);

    return ApiResponse.success("Đặt lại mật khẩu thành công");
  }

  // === Private helper methods ===

  /**
   * Logout - stateless JWT, just return success (client clears token)
   */
  public ApiResponse<Void> logout() {
    return ApiResponse.success("Đăng xuất thành công", null);
  }

  /**
   * Refresh access token using refresh token
   */
  public ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();

    if (!jwtService.validateToken(refreshToken)) {
      throw new BadRequestException("Refresh token không hợp lệ hoặc đã hết hạn");
    }

    String email = jwtService.extractEmail(refreshToken);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

    AuthResponse authResponse = generateAuthResponse(user);
    return ApiResponse.success("Làm mới token thành công", authResponse);
  }

  /**
   * Get current user profile from authenticated user
   */
  public ApiResponse<AuthResponse.UserInfo> getCurrentUser(User user) {
    AuthResponse.UserInfo userInfo =
        AuthResponse.UserInfo.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .phoneNumber(user.getPhoneNumber())
            .role(user.getRole().getRoleName())
            .isEmailVerified(Boolean.TRUE.equals(user.getIsEmailVerified()))
            .build();

    return ApiResponse.success("Lấy thông tin thành công", userInfo);
  }

  private AuthResponse generateAuthResponse(User user) {
    String accessToken =
        jwtService.generateToken(
            user.getEmail(), user.getRole().getRoleName(), user.getUserId(), user.getFullName());

    String refreshToken =
        jwtService.generateRefreshToken(user.getEmail(), user.getUserId());

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(jwtService.getTokenExpiration() / 1000) // Convert to seconds
        .user(
            AuthResponse.UserInfo.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().getRoleName())
                .isEmailVerified(Boolean.TRUE.equals(user.getIsEmailVerified()))
                .build())
        .build();
  }

  private void createEmailVerification(User user, String otpCode) {
    EmailVerification verification =
        EmailVerification.builder()
            .user(user)
            .otpCode(otpCode)
            .expiredAt(Instant.now().plus(otpExpirationMinutes, ChronoUnit.MINUTES))
            .verified(false)
            .build();

    emailVerificationRepository.save(verification);
  }

  private String generateOtp() {
    SecureRandom random = new SecureRandom();
    int otp = 100000 + random.nextInt(900000);
    return String.valueOf(otp);
  }
}
