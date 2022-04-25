package com.tsdl.service.configuration;

import com.tsdl.implementation.TsdlDataService;
import com.tsdl.infrastructure.DataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    DataService provideDataService() {
        return new TsdlDataService();
    }
}
