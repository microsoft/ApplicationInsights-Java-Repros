package com.peri.psd.testapp.playground.telemetry.filter;

import io.opentelemetry.api.trace.Span;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Slf4j
public abstract class AbstractLoggingFilter extends OncePerRequestFilter {


    final static String KEY_CALLER = "caller";
    final static String KEY_COUNTRY = "country";
    final static String KEY_REQUESTQUERY = "requestQuery";
    final static String KEY_REQUESTBODYRAW = "requestBodyRaw";

    // List of URL patterns to be exclude
    private static final List<String> EXCLUDED_PATHS = List.of(
        "/actuator"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return "/".equals(path) || EXCLUDED_PATHS.stream()
            .anyMatch(path::startsWith);
    }

    public void sendToSpan(String key, String value) {

        Span currentSpan = Span.current();
        String spanKind = MDC.get("MDC.span.kind");

        if (currentSpan != null && currentSpan.getSpanContext()
            .isValid() && "SERVER".equals(spanKind)) {

            log.debug("Span: Kind={} - Attribute: {}={}", spanKind, key, value);

            MDC.put("MDC." + key, value);

            currentSpan.setAttribute(key, value);
        } else {
            log.debug("REQUEST Attribute: {} = {}", key, value);
        }

    }


}
