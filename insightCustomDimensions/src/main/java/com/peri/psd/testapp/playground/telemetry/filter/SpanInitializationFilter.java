package com.peri.psd.testapp.playground.telemetry.filter;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(SecurityProperties.DEFAULT_FILTER_ORDER + 100)// Ensure it runs after the Security filters
@RequiredArgsConstructor
@Slf4j
public class SpanInitializationFilter extends AbstractLoggingFilter {

    private final Tracer tracer;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        // Create and activate a span for this request
        Span span = tracer.spanBuilder(request.getMethod() + " " + request.getRequestURI())
            .setSpanKind(SpanKind.SERVER)
            .startSpan();
        try (Scope scope = span.makeCurrent()) {
            SpanContext spanContext = span.getSpanContext();
            MDC.put("MDC.span.kind", SpanKind.SERVER.name());
            span.setAttribute("span.kind", SpanKind.SERVER.name());
            log.debug("current SPAN {}", spanContext);

            filterChain.doFilter(request, response);
        } finally {
            log.debug("END current SPAN: {}", span.getSpanContext());
            MDC.clear();
            span.end();
        }
    }

}
