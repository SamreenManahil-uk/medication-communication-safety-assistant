package com.samreen.medicationsafety.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiServiceConfig {

    @Value("${ai.service.base-url:http://127.0.0.1:8000}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
}
