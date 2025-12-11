package com.ctf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")             // ко всем
                .excludePathPatterns(
                        "/",
                        "/auth",
                        "/login",
                        "/check-username",
                        "/check-email",
                        "/top3",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error",
                        "/debug",
                        "/api/challenges/validate"
                );
    }
}
