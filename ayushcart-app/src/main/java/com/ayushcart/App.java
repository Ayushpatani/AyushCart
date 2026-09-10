package com.ayushcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the AyushCart backend.
 * {@code @SpringBootApplication} turns on component scanning for every class
 * under the {@code com.ayushcart} package, plus Spring Boot auto-configuration.
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
