package com.vishal.ordering.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(2)
public class SimpleRateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, RequestWindow> windows = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.max-requests-per-minute:30}")
    private int maxRequestsPerMinute;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientKey = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .filter(value -> !value.isBlank())
                .orElse(request.getRemoteAddr());

        long now = System.currentTimeMillis();
        RequestWindow window = windows.compute(clientKey, (key, existing) -> {
            if (existing == null || now - existing.windowStartMillis() >= 60_000) {
                return new RequestWindow(now, new AtomicInteger(0));
            }
            return existing;
        });

        int requestCount = window.counter().incrementAndGet();
        if (requestCount > maxRequestsPerMinute) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"message":"Rate limit exceeded. Try again in a minute."}
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private record RequestWindow(long windowStartMillis, AtomicInteger counter) {
    }
}
