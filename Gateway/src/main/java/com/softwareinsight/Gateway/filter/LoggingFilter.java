package com.softwareinsight.Gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global Logging Filter
 * GlobalFilter applies to all routes automatically
 * Ordered.HIGHEST_PRECEDENCE ensures this runs first
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Log request
        log.info(">>> Request: {} {} from {}",
                request.getMethod(),
                request.getURI(),
                request.getRemoteAddress());

        // Log headers (excluding sensitive ones)
        request.getHeaders().forEach((name, values) -> {
            if (!name.equalsIgnoreCase("Authorization")) {
                log.debug(">>> Header: {} = {}", name, values);
            }
        });

        // Record start time
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Log response
            log.info("<<< Response: {} {} - Status: {} - Duration: {}ms",
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    duration);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}