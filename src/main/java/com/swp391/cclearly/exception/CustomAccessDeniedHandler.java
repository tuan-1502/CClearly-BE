package com.swp391.cclearly.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");

    String jsonResponse = String.format(
        "{\"success\":false,\"status\":403,\"message\":\"Bạn không có quyền truy cập tài nguyên này\",\"path\":\"%s\"}",
        request.getRequestURI()
    );

    PrintWriter out = response.getWriter();
    out.print(jsonResponse);
    out.flush();
  }
}
