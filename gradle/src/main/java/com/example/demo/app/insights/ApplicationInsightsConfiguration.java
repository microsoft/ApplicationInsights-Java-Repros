package com.example.demo.app.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationInsightsConfiguration {
    @Bean
    public TelemetryClient telemetryClient() {
        return new TelemetryClient();
    }
}