package com.educacaofinanceira.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final Bandwidth loginBandwidth;
    private final Bandwidth registerBandwidth;
    private final Bandwidth refreshBandwidth;
    private final Bandwidth generalBandwidth;
    private final ConcurrentHashMap<String, Bucket> loginBucketCache;
    private final ConcurrentHashMap<String, Bucket> registerBucketCache;
    private final ConcurrentHashMap<String, Bucket> refreshBucketCache;
    private final ConcurrentHashMap<String, Bucket> generalBucketCache;

    public RateLimitingFilter(
            ObjectMapper objectMapper,
            @Qualifier("loginBandwidth") Bandwidth loginBandwidth,
            @Qualifier("registerBandwidth") Bandwidth registerBandwidth,
            @Qualifier("refreshBandwidth") Bandwidth refreshBandwidth,
            @Qualifier("generalBandwidth") Bandwidth generalBandwidth,
            @Qualifier("loginBucketCache") ConcurrentHashMap<String, Bucket> loginBucketCache,
            @Qualifier("registerBucketCache") ConcurrentHashMap<String, Bucket> registerBucketCache,
            @Qualifier("refreshBucketCache") ConcurrentHashMap<String, Bucket> refreshBucketCache,
            @Qualifier("generalBucketCache") ConcurrentHashMap<String, Bucket> generalBucketCache) {
        this.objectMapper = objectMapper;
        this.loginBandwidth = loginBandwidth;
        this.registerBandwidth = registerBandwidth;
        this.refreshBandwidth = refreshBandwidth;
        this.generalBandwidth = generalBandwidth;
        this.loginBucketCache = loginBucketCache;
        this.registerBucketCache = registerBucketCache;
        this.refreshBucketCache = refreshBucketCache;
        this.generalBucketCache = generalBucketCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = extractClientIp(request);

        Bucket bucket = resolveBucket(method, path, clientIp);

        if (bucket == null) {
            filterChain.doFilter(request, response);
            return;
        }

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitSeconds = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            writeRateLimitResponse(response, waitSeconds);
        }
    }

    private Bucket resolveBucket(String method, String path, String clientIp) {
        if ("POST".equalsIgnoreCase(method) && path.equals("/api/auth/login")) {
            return loginBucketCache.computeIfAbsent(clientIp,
                    k -> Bucket.builder().addLimit(loginBandwidth).build());
        }
        if ("POST".equalsIgnoreCase(method) && path.equals("/api/auth/register")) {
            return registerBucketCache.computeIfAbsent(clientIp,
                    k -> Bucket.builder().addLimit(registerBandwidth).build());
        }
        if ("POST".equalsIgnoreCase(method) && path.equals("/api/auth/refresh")) {
            return refreshBucketCache.computeIfAbsent(clientIp,
                    k -> Bucket.builder().addLimit(refreshBandwidth).build());
        }
        if (path.startsWith("/api/")) {
            return generalBucketCache.computeIfAbsent(clientIp,
                    k -> Bucket.builder().addLimit(generalBandwidth).build());
        }
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeRateLimitResponse(HttpServletResponse response,
                                        long retryAfterSeconds) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("Retry-After", String.valueOf(retryAfterSeconds));

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("message", "Muitas requisições. Tente novamente em " + retryAfterSeconds + " segundo(s).");
        body.put("timestamp", LocalDateTime.now().toString());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
