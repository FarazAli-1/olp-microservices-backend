package com.centegy.enrollment_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced // This tells WebClient to use Eureka to find other services
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}