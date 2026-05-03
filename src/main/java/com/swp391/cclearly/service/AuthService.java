package com.swp391.cclearly.service;

import com.swp391.cclearly.config.JwtService;
import com.swp391.cclearly.dto.auth.AuthResponse;
import com.swp391.cclearly.dto.auth.ForgotPasswordRequest;
import com.swp391.cclearly.dto.auth.LoginRequest;
import com.swp391.cclearly.dto.auth.RefreshTokenRequest;
import com.swp391.cclearly.dto.auth.RegisterRequest;
import com.swp391.cclearly.dto.auth.ResendOtpRequest;
import com.swp391.cclearly.dto.auth.ResetPasswordRequest;
import com.swp391.cclearly.dto.auth.VerifyEmailRequest;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.entity.EmailVerification;
import com.swp391.cclearly.entity.LoginSession;
import com.swp391.cclearly.entity.PasswordResetToken;
import com.swp391.cclearly.entity.Role;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.EmailVerificationRepository;
import com.swp391.cclearly.repository.LoginSessionRepository;
import com.swp391.cclearly.repository.PasswordResetTokenRepository;
import com.swp391.cclearly.repository.RoleRepository;
import com.swp391.cclearly.repository.UserRepository;
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
  private final LoginSessionRepository loginSessionRepository;

  @Value("${app.otp.expiration-minutes:5}")
  private int otpExpirationMinutes;

  @Value("${app.password-reset.expiration-minutes:30}")
  private int passwordResetExpirationMinutes;

  private static final String DEFAULT_ROLE = "CUSTOMER";
  private static final String USER_STATUS_ACTIVE = "ACTIVE";
  private static final String USER_STATUS_PENDING = "PENDING_VERIFICATION";

  @Transactional
  public ApiResponse<AuthResponse> register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email da duoc su dung");
    }

    if (request.getPhoneNumber() != null
        && !request.getPhoneNumber().isEmpty()
        && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
      throw new BadRequestException("So dien thoai da duoc su dung");
    }

    Role customerRole =
        roleRepository
            .findByRoleName(DEFAULT_ROLE)
            .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay role CUSTOMER"));

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

    String otpCode = generateOtp();
    createEmailVerification(user, otpCode);
    emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);

    AuthResponse authResponse = generateAuthResponse(user);
    return ApiResponse.success("Dang ky thanh cong. Vui long xac thuc email.", authResponse);
  }

  @Transactional
  public ApiResponse<AuthResponse> login(LoginRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (BadCredentialsException e) {
      throw new BadRequestException("Email hoac mat khau khong dung");
    }

    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("Email hoac mat khau khong dung"));

    if (!USER_STATUS_ACTIVE.equals(user.getStatus()) && !USER_STATUS_PENDING.equals(user.getStatus())) {
      throw new BadRequestException("Tai khoan da bi khoa hoac vo hieu hoa");
    }

    user.setLastLogin(Instant.now());
    userRepository.save(user);

    AuthResponse authResponse = generateAuthResponse(user);

    auditLogService.log(user, "LOGIN", "Dang nhap thanh cong: " + user.getEmail());
    return ApiResponse.success("Dang nhap thanh cong", authResponse);
  }

  @Transactional
  public ApiResponse<Void> verifyEmail(VerifyEmailRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi dung"));

    if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
      throw new BadRequestException("Email da duoc xac thuc truoc do");
    }

    EmailVerification verification =
        emailVerificationRepository
            .findByUserAndOtpCodeAndVerifiedFalse(user, request.getOtpCode())
            .orElseThrow(() -> new BadRequestException("Ma OTP khong dung"));

    if (verification.getExpiredAt().isBefore(Instant.now())) {
      throw new BadRequestException("Ma OTP da het han");
    }

    verification.setVerified(true);
    emailVerificationRepository.save(verification);

    user.setIsEmailVerified(true);
    user.setStatus(USER_STATUS_ACTIVE);
    userRepository.save(user);

    return ApiResponse.success("Xac thuc email thanh cong");
  }

  @Transactional
  public ApiResponse<Void> resendOtp(ResendOtpRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi dung"));

    if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
      throw new BadRequestException("Email da duoc xac thuc truoc do");
    }

    String otpCode = generateOtp();
    createEmailVerification(user, otpCode);
    emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);

    return ApiResponse.success("Da gui lai ma OTP. Vui long kiem tra email.");
  }

  @Transactional
  public ApiResponse<Void> forgotPassword(ForgotPasswordRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi dung voi email nay"));

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
    emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), token);

    return ApiResponse.success("Da gui link dat lai mat khau den email cua ban");
  }

  @Transactional
  public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
    PasswordResetToken resetToken =
        passwordResetTokenRepository
            .findByTokenAndUsedFalse(request.getToken())
            .orElseThrow(() -> new BadRequestException("Token khong hop le hoac da duoc su dung"));

    if (!resetToken.isValid()) {
      throw new BadRequestException("Token da het han");
    }

    User user = resetToken.getUser();
    user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    resetToken.setUsed(true);
    passwordResetTokenRepository.save(resetToken);
    loginSessionRepository.deactivateAllSessionsByUser(user);

    return ApiResponse.success("Dat lai mat khau thanh cong");
  }

  public ApiResponse<Void> logout(User user) {
    loginSessionRepository.deactivateAllSessionsByUser(user);
    return ApiResponse.success("Dang xuat thanh cong", null);
  }

  @Transactional
  public ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();

    if (!jwtService.validateToken(refreshToken)) {
      throw new BadRequestException("Refresh token khong hop le hoac da het han");
    }

    if (!"refresh".equalsIgnoreCase(jwtService.extractTokenType(refreshToken))) {
      throw new BadRequestException("Token cung cap khong phai refresh token");
    }

    LoginSession loginSession =
        loginSessionRepository
            .findByRefreshTokenAndIsActiveTrue(refreshToken)
            .orElseThrow(() -> new BadRequestException("Refresh token da bi thu hoi hoac khong ton tai"));

    if (!loginSession.isValid()) {
      loginSession.setIsActive(false);
      loginSessionRepository.save(loginSession);
      throw new BadRequestException("Refresh token khong hop le hoac da het han");
    }

    String email = jwtService.extractEmail(refreshToken);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi dung"));

    if (!loginSession.getUser().getUserId().equals(user.getUserId())) {
      throw new BadRequestException("Refresh token khong khop voi nguoi dung");
    }

    loginSession.setIsActive(false);
    loginSession.setLastAccessedAt(Instant.now());
    loginSessionRepository.save(loginSession);

    AuthResponse authResponse = generateAuthResponse(user);
    return ApiResponse.success("Lam moi token thanh cong", authResponse);
  }

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

    return ApiResponse.success("Lay thong tin thanh cong", userInfo);
  }

  private AuthResponse generateAuthResponse(User user) {
    String accessToken =
        jwtService.generateToken(
            user.getEmail(), user.getRole().getRoleName(), user.getUserId(), user.getFullName());

    String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getUserId());
    persistLoginSession(user, refreshToken);

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(jwtService.getTokenExpiration() / 1000)
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

  private void persistLoginSession(User user, String refreshToken) {
    LoginSession session =
        LoginSession.builder()
            .user(user)
            .refreshToken(refreshToken)
            .expiredAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()))
            .lastAccessedAt(Instant.now())
            .build();
    loginSessionRepository.save(session);
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
