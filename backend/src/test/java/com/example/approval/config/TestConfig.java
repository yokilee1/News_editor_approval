package com.example.approval.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@ComponentScan(basePackages = {"com.example.approval.controller", "com.example.approval.security"})
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
@EnableWebSecurity
public class TestConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .anyRequest().permitAll();
        return http.build();
    }
} 