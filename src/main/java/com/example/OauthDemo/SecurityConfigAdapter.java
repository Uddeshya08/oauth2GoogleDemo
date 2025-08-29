package com.example.OauthDemo;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface SecurityConfigAdapter {
    void configure(HttpSecurity http) throws Exception;
}
