package com.notitficationService.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericConfiguration {

    @Bean
    ObjectMapper objectMapper(){
          return new ObjectMapper(); //converts string to json and vice versa
    }
}
