package com.did.docdiffserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 允许所有路径
//                        .allowedOrigins("http://localhost:5174")
                        .allowedOriginPatterns("*")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                        .allowedHeaders("*") // 允许所有请求头
                        .maxAge(3600); // 预检请求缓存时间（秒）
            }
        };
    }


    /**
     *
     *     .allowedOriginPatterns("*") // 允许所有域名
     *     .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
     *     .allowedHeaders("*")
     *     .allowCredentials(true)
     *     .maxAge(3600);
     *
     */
}
