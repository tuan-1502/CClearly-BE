package com.swp391.cclearly.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.expiration:86400000}")
  private long tokenExpiration; // Default 24 hours

  @Value("${jwt.refresh-expiration:604800000}")
  private long refreshTokenExpiration; // Default 7 days

  private SecretKey key;

  @PostConstruct
  public void init() {
    log.info("JWT Service initialized");
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generate JWT token with user info and role
   */
  public String generateToken(String email, String role, UUID userId, String fullName) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    claims.put("userId", userId.toString());
    claims.put("fullName", fullName != null ? fullName : "");

    return Jwts.builder()
        .claims(claims)
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
        .signWith(key)
        .compact();
  }

  /**
   * Generate long-lived refresh token
   */
  public String generateRefreshToken(String email, UUID userId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId.toString());
    claims.put("type", "refresh");

    return Jwts.builder()
        .claims(claims)
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
        .signWith(key)
        .compact();
  }

  /**
   * Extract email (subject) from token
   */
  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extract user ID from token
   */
  public UUID extractUserId(String token) {
    String userId = extractClaim(token, claims -> claims.get("userId", String.class));
    return userId != null ? UUID.fromString(userId) : null;
  }

  /**
   * Extract role from token
   */
  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  /**
   * Validate token structure and signature
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("JWT token expired");
    } catch (MalformedJwtException e) {
      log.warn("Invalid JWT token");
    } catch (UnsupportedJwtException e) {
      log.warn("Unsupported JWT token");
    } catch (SignatureException e) {
      log.warn("Invalid JWT signature");
    } catch (IllegalArgumentException e) {
      log.warn("JWT claims string is empty");
    }
    return false;
  }

  /**
   * Check if token is valid for a specific email
   */
  public boolean isTokenValid(String token, String email) {
    try {
      final String tokenEmail = extractEmail(token);
      return tokenEmail.equals(email) && !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public long getTokenExpiration() {
    return tokenExpiration;
  }

  public long getRefreshTokenExpiration() {
    return refreshTokenExpiration;
  }
}
