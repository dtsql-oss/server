package org.tsdl.service.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
  @Value("${application.version}")
  private String applicationVersion;

  @Bean
  public OpenAPI serviceApi() {
    var appVersion = applicationVersion != null ? applicationVersion : "unknown version";
    return new OpenAPI()
        .components(new Components())
        .info(new Info()
            .title("TSDL Service")
            .version(appVersion)
            .description("A REST service exposing TSDL Query capabilities.")
            .termsOfService("TOS")
            .contact(new Contact()
                .name("Raffael Foidl")
                .url("http://www.google.com")
                .email("raffael.foidl@student.tuwien.ac.at")));
  }
}
