package com.softwareinsight.UserService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

/**
 * * Feign Client Configuration
 */
@Configuration
public class FeignConfig {
    /**
     * Feign logger level
     * FULL logs headers, body, and metadata for both request and response
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}