package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.logging.OpenTelemetryLoggingAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
@SpringBootApplication(exclude = {OpenTelemetryLoggingAutoConfiguration.class})
public class LogsToAzureApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(LogsToAzureApplication.class, args);
    }
}
