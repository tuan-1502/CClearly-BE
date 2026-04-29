package com.swp391.cclearly.config;

import com.swp391.cclearly.exception.CustomAccessDeniedHandler;
import com.swp391.cclearly.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(request -> {
          var corsConfig = new org.springframework.web.cors.CorsConfiguration();
          corsConfig.addAllowedOriginPattern("*");
          corsConfig.addAllowedMethod("*");
          corsConfig.addAllowedHeader("*");
          corsConfig.setAllowCredentials(true);
          return corsConfig;
        }))
        .authorizeHttpRequests(auth -> auth
            // 🔒 Auth endpoints that require authentication
            .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

            // ✅ Public endpoints - Auth APIs
            .requestMatchers(
                "/api/auth/**",
                "/api/public/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/v3/api-docs",
                "/webjars/**",
                "/actuator/**"
            ).permitAll()

            // ✅ Public GET endpoints - Products
            .requestMatchers(HttpMethod.GET,
                "/api/products/**",
                "/api/frames/**",
                "/api/lenses/**",
                "/api/accessories/**",
                "/api/categories/**"
            ).permitAll()

            // 🔒 Admin endpoints shared with Manager (dashboard, revenue, users)
            .requestMatchers("/api/admin/dashboard/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers("/api/admin/revenue/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers("/api/admin/users/**").hasAnyRole("ADMIN", "MANAGER")

            // 🔒 Admin only endpoints (settings, logs, etc.)
            .requestMatchers("/api/admin/**").hasRole("ADMIN")

            // 🔒 Manager endpoints
            .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")

            // 🔒 Sales Staff endpoints
            .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("ADMIN", "MANAGER", "SALES_STAFF")
            .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN", "MANAGER", "SALES_STAFF")
            .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("ADMIN", "MANAGER")

            // 🔒 Operation Staff endpoints
            .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "MANAGER", "OPERATION_STAFF")
            .requestMatchers("/api/shipping/**").hasAnyRole("ADMIN", "MANAGER", "OPERATION_STAFF")

            // 🔒 Returns/Refunds - Sales & Operation staff
            .requestMatchers("/api/returns/**").hasAnyRole("ADMIN", "MANAGER", "SALES_STAFF", "OPERATION_STAFF")

            // 🔒 Promotions - Admin & Manager
            .requestMatchers("/api/promotions/**").hasAnyRole("ADMIN", "MANAGER")

            // ✅ Banners - Active banners public, management Admin/Manager only
            .requestMatchers(HttpMethod.GET, "/api/banners/active").permitAll()
            .requestMatchers("/api/banners/**").hasAnyRole("ADMIN", "MANAGER")

            // 🔒 Customer endpoints
            .requestMatchers("/api/users/**").hasAnyRole("CUSTOMER", "ADMIN", "MANAGER", "SALES_STAFF", "OPERATION_STAFF")
            .requestMatchers("/api/orders/**").authenticated()
            .requestMatchers("/api/cart/**").authenticated()

            // 🔒 All other requests require authentication
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .accessDeniedHandler(customAccessDeniedHandler)
            .authenticationEntryPoint(customAuthenticationEntryPoint)
        )
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
