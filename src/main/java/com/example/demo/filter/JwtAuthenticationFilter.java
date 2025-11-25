package com.example.demo.filter;

import com.example.demo.util.JwtUtility;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthenticationFilter implements Filter {

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(401);
      response.getWriter().write("Unauthorized - Missing JWT");
      return;
    }

    String token = authHeader.substring(7);

    try {
      Integer userId = JwtUtility.extractUserId(token);

      request.setAttribute("authUserId", userId);

    } catch (Exception e) {
      response.setStatus(401);
      response.getWriter().write("Unauthorized - Invalid JWT");
      return;
    }

    chain.doFilter(req, res);
  }
}

