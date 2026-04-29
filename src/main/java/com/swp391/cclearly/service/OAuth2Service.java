package com.swp391.cclearly.service;

import com.swp391.cclearly.config.JwtService;
import com.swp391.cclearly.dto.auth.AuthResponse;
import com.swp391.cclearly.dto.auth.GoogleLoginRequest;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.entity.Role;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.RoleRepository;
import com.swp391.cclearly.repository.UserRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final JwtService jwtService;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String googleClientId;

  private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";
  private static final String DEFAULT_ROLE = "CUSTOMER";

  /**
   * Login or register with Google
   */
  @Transactional
  public ApiResponse<AuthResponse> googleLogin(GoogleLoginRequest request) {
    // Verify Google ID Token
    Map<String, Object> googleUser = verifyGoogleToken(request.getIdToken());

    String email = (String) googleUser.get("email");
    String fullName = (String) googleUser.get("name");
    Boolean emailVerified = Boolean.parseBoolean((String) googleUser.get("email_verified"));

    if (!emailVerified) {
      throw new BadRequestException("Email Google chưa được xác thực");
    }

    // Find or create user
    Optional<User> existingUser = userRepository.findByEmail(email);
    User user;

    if (existingUser.isPresent()) {
      user = existingUser.get();
      // Update last login
      user.setLastLogin(Instant.now());
      userRepository.save(user);
      log.info("Google login: existing user {}", email);
    } else {
      // Create new user
      Role customerRole = roleRepository.findByRoleName(DEFAULT_ROLE)
          .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role CUSTOMER"));

      user = User.builder()
          .email(email)
          .fullName(fullName)
          .passwordHash("") // No password for OAuth users
          .role(customerRole)
          .status("ACTIVE")
          .isEmailVerified(true) // Google already verified
          .createdAt(Instant.now())
          .lastLogin(Instant.now())
          .build();

      user = userRepository.save(user);
      log.info("Google login: created new user {}", email);
    }

    // Generate JWT token
    String accessToken = jwtService.generateToken(
        user.getEmail(),
        user.getRole().getRoleName(),
        user.getUserId(),
        user.getFullName()
    );

    String refreshToken = jwtService.generateRefreshToken(
        user.getEmail(),
        user.getUserId()
    );

    AuthResponse authResponse = AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(jwtService.getTokenExpiration() / 1000)
        .user(AuthResponse.UserInfo.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .phoneNumber(user.getPhoneNumber())
            .role(user.getRole().getRoleName())
            .isEmailVerified(true)
            .build())
        .build();

    return ApiResponse.success("Đăng nhập thành công", authResponse);
  }

  /**
   * Verify Google ID Token and return user info
   */
  private Map<String, Object> verifyGoogleToken(String idToken) {
    try {
      RestTemplate restTemplate = new RestTemplate();
      String url = GOOGLE_TOKEN_INFO_URL + idToken;

      ResponseEntity<Map> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          new HttpEntity<>(new HttpHeaders()),
          Map.class
      );

      Map<String, Object> body = response.getBody();

      if (body == null) {
        throw new BadRequestException("Không thể xác thực Google token");
      }

      // Verify the token is for our app
      String aud = (String) body.get("aud");
      if (!googleClientId.equals(aud)) {
        throw new BadRequestException("Google token không hợp lệ cho ứng dụng này");
      }

      return body;
    } catch (Exception e) {
      log.error("Failed to verify Google token: {}", e.getMessage());
      throw new BadRequestException("Google token không hợp lệ: " + e.getMessage());
    }
  }
}
