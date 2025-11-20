package com.softwareinsight.Gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Configuration
 * 
 * Design Patterns:
 * - Builder Pattern: RouteLocatorBuilder for fluent route definition
 * - Decorator Pattern: Filters decorate routes with additional behavior
 */
@Configuration
public class GatewayConfig {

    /**
     * Custom route locator
     * <p>
     * You can define routes programmatically here if needed
     * This provides more flexibility for complex routing logic
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Add custom headers to all auth requests
                .route("auth-service-custom", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "API-Gateway")
                                .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                        )
                        .uri("lb://auth-service")
                )
                .build();
    }
}