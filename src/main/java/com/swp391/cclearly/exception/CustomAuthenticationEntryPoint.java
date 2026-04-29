package com.swp391.cclearly.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");

    String jsonResponse = String.format(
        "{\"success\":false,\"status\":401,\"message\":\"Phiên đăng nhập không hợp lệ hoặc đã hết hạn\",\"path\":\"%s\"}",
        request.getRequestURI()
    );

    PrintWriter out = response.getWriter();
    out.print(jsonResponse);
    out.flush();
  }
}
