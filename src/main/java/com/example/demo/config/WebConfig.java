package com.example.demo.config;

import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.filter.NonceFilter;
import com.example.demo.filter.RateLimitFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebConfig {

  @Bean
  public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
    FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(new RateLimitFilter());
    registrationBean.addUrlPatterns("/api/leaderboard/*");
    registrationBean.setOrder(1);

    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthFilter() {
    FilterRegistrationBean<JwtAuthenticationFilter> bean = new FilterRegistrationBean<>();

    bean.setFilter(new JwtAuthenticationFilter());
    bean.addUrlPatterns("/api/leaderboard/submit");
    bean.setOrder(0);

    return bean;
  }

  @Bean
  public WebMvcConfigurer securityHeadersConfig() {
    return new WebMvcConfigurer() {
      @Override
      public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new HandlerInterceptor() {
          @Override
          public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
              Object handler) {

            response.setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self'; object-src 'none'; style-src 'self' "
                    + "'unsafe-inline';");

            response.setHeader("X-XSS-Protection", "1; mode=block");

            response.setHeader("X-Content-Type-Options", "nosniff");

            response.setHeader("X-Frame-Options", "DENY");

            return true;
          }
        });
      }
    };
  }

  @Bean
  public FilterRegistrationBean<NonceFilter> nonceFilterRegistration(NonceFilter filter) {
    FilterRegistrationBean<NonceFilter> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(filter);
    registrationBean.addUrlPatterns("/api/leaderboard/submit");
    registrationBean.setOrder(2);

    return registrationBean;
  }

}
