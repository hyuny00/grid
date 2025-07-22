package com.futechsoft.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

//ApplicationConfig.java - 앱 설정
@Configuration

public class ApplicationConfig {
 
 @Bean
 public RestTemplate restTemplate() {
     return new RestTemplate();
 }
}