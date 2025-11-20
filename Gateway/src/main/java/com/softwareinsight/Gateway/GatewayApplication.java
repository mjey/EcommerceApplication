package com.softwareinsight.Gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 *
 * Design Patterns:
 * - Gateway Pattern: Single entry point for all microservices
 * - Service Discovery: Dynamic routing via Eureka
 * - Circuit Breaker: Fault tolerance
 * - Rate Limiting: Protection against abuse
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
