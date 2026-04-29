package com.swp391.cclearly.config;

import com.swp391.cclearly.repository.SystemConfigRepository;
import com.swp391.cclearly.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final SystemConfigRepository systemConfigRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");

    // Skip if no Authorization header or not Bearer token
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final String token = authHeader.substring(7);
      final String email = jwtService.extractEmail(token);

      // If email extracted and no authentication in context
      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        // Find user by email
        var userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
          var user = userOptional.get();

          // Validate token
          if (jwtService.isTokenValid(token, email)) {
            // Extract role from token
            String role = jwtService.extractRole(token);

            // During maintenance, reject all non-ADMIN requests
            if (!"ADMIN".equals(role) && isMaintenanceMode()) {
              response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write(
                  "{\"success\":false,\"message\":\"Hệ thống đang bảo trì\"}");
              return;
            }

            // Create authentication with role
            var authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);

            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated user: {} with role: {}", email, role);
          }
        }
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private boolean isMaintenanceMode() {
    return systemConfigRepository.findByConfigKey("maintenance_mode")
        .map(c -> "true".equalsIgnoreCase(c.getConfigValue()))
        .orElse(false);
  }
}
