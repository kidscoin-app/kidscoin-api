package com.educacaofinanceira.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitingConfig {

    // Login: 5 tentativas por minuto por IP
    @Bean("loginBandwidth")
    public Bandwidth loginBandwidth() {
        return Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(1))
                .build();
    }

    // Registro: 3 tentativas a cada 10 minutos por IP
    @Bean("registerBandwidth")
    public Bandwidth registerBandwidth() {
        return Bandwidth.builder()
                .capacity(3)
                .refillIntervally(3, Duration.ofMinutes(10))
                .build();
    }

    // Refresh de token: 10 tentativas por minuto por IP
    @Bean("refreshBandwidth")
    public Bandwidth refreshBandwidth() {
        return Bandwidth.builder()
                .capacity(10)
                .refillIntervally(10, Duration.ofMinutes(1))
                .build();
    }

    // API geral: 60 requisições por minuto por IP
    @Bean("generalBandwidth")
    public Bandwidth generalBandwidth() {
        return Bandwidth.builder()
                .capacity(60)
                .refillGreedy(60, Duration.ofMinutes(1))
                .build();
    }

    @Bean("loginBucketCache")
    public ConcurrentHashMap<String, Bucket> loginBucketCache() {
        return new ConcurrentHashMap<>();
    }

    @Bean("registerBucketCache")
    public ConcurrentHashMap<String, Bucket> registerBucketCache() {
        return new ConcurrentHashMap<>();
    }

    @Bean("refreshBucketCache")
    public ConcurrentHashMap<String, Bucket> refreshBucketCache() {
        return new ConcurrentHashMap<>();
    }

    @Bean("generalBucketCache")
    public ConcurrentHashMap<String, Bucket> generalBucketCache() {
        return new ConcurrentHashMap<>();
    }
}
