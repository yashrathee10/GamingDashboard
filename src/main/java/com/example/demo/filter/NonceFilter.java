package com.example.demo.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class NonceFilter implements Filter {

  @Autowired
  private RedisTemplate<String, String> redis;

  private static final long NONCE_TTL_SECONDS = 1;

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    String nonce = request.getHeader("X-Nonce");

    if (nonce == null) {
      response.setStatus(400);
      response.getWriter().write("Missing nonce");
      return;
    }

    Boolean exists = redis.opsForValue().setIfAbsent(
        "nonce:" + nonce, "1", NONCE_TTL_SECONDS, TimeUnit.MINUTES);

    if (!exists) {
      response.setStatus(409);
      response.getWriter().write("Replay detected. Nonce reuse.");
      return;
    }

    chain.doFilter(req, res);
  }
}