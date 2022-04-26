package org.tsdl.service.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI serviceApi() {
        return new OpenAPI()
          .components(new Components())
          .info(new Info()
            .title("TSDL Service")
            .version("0.1-SNAPSHOT") // TODO: mechanism to automatically keep in sync with ${revision}, both in dev and production mode
            .description("A REST service exposing TSDL Query capabilities.")
            .termsOfService("TOS")
            .contact(new Contact()
              .name("Raffael Foidl")
              .url("http://www.google.com")
              .email("raffael.foidl@student.tuwien.ac.at")));
    }
}
