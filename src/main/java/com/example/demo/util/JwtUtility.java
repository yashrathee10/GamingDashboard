package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtility {

  private static final String SECRET_KEY = "gB2skF4P0C1M0x8xE7Zou0J3W9tShzDxGYD1YcRb7n8=";

  public static Integer extractUserId(String token) {
    Claims claims =
        Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

    return claims.get("userId", Integer.class);
  }

  public static String generateToken(Long userId, String username) {

    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);
    claims.put("username", username);

    return Jwts.builder().setClaims(claims).setSubject(String.valueOf(userId))
        .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
  }

  private static Key getSigningKey() {
    byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}