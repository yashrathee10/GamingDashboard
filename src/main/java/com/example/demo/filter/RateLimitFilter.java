package com.example.demo.filter;

import com.example.demo.util.JwtUtility;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RateLimitFilter implements Filter {

  private final Map<Integer, Bucket> bucketCache = new ConcurrentHashMap<>();

  private Bucket createNewBucket() {
    return Bucket.builder().addLimit(Bandwidth.classic(6, Refill.greedy(2, Duration.ofMinutes(1))))
        .build();
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(401);
      response.getWriter().write("Missing or invalid token");
      return;
    }

    String token = authHeader.substring(7);

    Integer userId;
    try {
      userId = JwtUtility.extractUserId(token);
    } catch (Exception e) {
      response.setStatus(401);
      response.getWriter().write("Invalid JWT token");
      return;
    }

    Bucket bucket = bucketCache.computeIfAbsent(userId, k -> createNewBucket());

    if (bucket.tryConsume(1)) {
      chain.doFilter(req, res);
    } else {
      response.setStatus(429);
      response.getWriter().write("Rate limit exceeded for user " + userId);
    }
  }
}