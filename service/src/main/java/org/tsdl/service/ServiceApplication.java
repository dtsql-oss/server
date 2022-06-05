package org.tsdl.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application representing service exposing TSDL query capabilities.
 */
@SpringBootApplication
public class ServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(ServiceApplication.class, args);
  }
}
