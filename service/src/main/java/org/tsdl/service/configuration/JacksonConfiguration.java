package org.tsdl.service.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class JacksonConfiguration {

    @Primary
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
          .featuresToEnable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
          .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, DeserializationFeature.ACCEPT_FLOAT_AS_INT)
          .indentOutput(true)
          .findModulesViaServiceLoader(true)
          .modules(new JavaTimeModule())
          .timeZone(TimeZone.getTimeZone(ZoneId.systemDefault())) // serialize ZonedDateTime in primaryTimeZone
          .build();
    }
}
