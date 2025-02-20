package com.github.progxaker;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenTelemetryDemo {
    // Set the logger to reproduce the issue
    private static final Logger logger = LoggerFactory.getLogger(OpenTelemetryDemo.class);

    public static void main(String[] args) {
        OpenTelemetryDemo demo = new OpenTelemetryDemo();
        demo.performOperation();
    }

    @WithSpan
    public void performOperation() {
        try {
            // Simulating an exception
            throw new RuntimeException("Something went wrong!");
        } catch (Throwable e) {
            logger.error("Error during processing", e);
            // Record the exception
            Span.current().recordException(e);
            throw e;
        }
    }
}
