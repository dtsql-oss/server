package org.tsdl.service.configuration;

import org.tsdl.implementation.TsdlDataService;
import org.tsdl.infrastructure.DataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    DataService provideDataService() {
        return new TsdlDataService();
    }
}
