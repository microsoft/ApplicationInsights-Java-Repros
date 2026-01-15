package com.example;

import com.azure.monitor.opentelemetry.autoconfigure.AzureMonitorAutoConfigure;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdkBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelemetryLogToAzureConfig {
    @Value("${connection_string}")
    private String connectionString;
    @Bean
    public OpenTelemetrySdk openTelemetrywithConnectionString() {
        AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
        AzureMonitorAutoConfigure.customize(sdkBuilder, connectionString);
        OpenTelemetrySdk sdk = sdkBuilder.build().getOpenTelemetrySdk();
        // Install the OpenTelemetry instance to the Log4j2 appender
        OpenTelemetryAppender.install(sdk);
        return sdk;
    }

    @Bean
    public static Span tracer(OpenTelemetrySdk sdk) {
        return sdk.getTracer("com.example.log").spanBuilder("logs-to-azure").startSpan();
    }

    @Bean
    public io.opentelemetry.api.logs.Logger otelLogger(OpenTelemetrySdk sdk) {
        return sdk.getSdkLoggerProvider().loggerBuilder("com.example.log").build();
    }

}

