package com.github.progxaker;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;

public class OpenTelemetryDemo {
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
            // Record the exception
            Span.current().recordException(e);
            throw e;
        }
    }
}
