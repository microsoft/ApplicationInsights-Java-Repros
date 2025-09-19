package com.peri.psd.testapp.playground.telemetry.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(name = "psd.azure.insight.filter.body-filter", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
@Order(SecurityProperties.DEFAULT_FILTER_ORDER + 140)
public class RawBodyLoggingFilter extends AbstractLoggingFilter {

    private final int MAX_BODY_SIZE = 5000;

    // https://petrepopescu.tech/2023/07/how-to-log-http-request-and-response-in-spring-boot/
    // https://www.linkedin.com/pulse/rest-api-monitoring-azure-application-insights-srinivas-anumala
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        RepeatableContentCachingRequestWrapper requestWrapper = new RepeatableContentCachingRequestWrapper(
            request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        logRequest(requestWrapper);

        filterChain.doFilter(requestWrapper, responseWrapper);
        logResponse(responseWrapper);

    }

    private void logRequest(
        RepeatableContentCachingRequestWrapper requestWrapper) throws IOException {

        // this must be read always
        String body = requestWrapper.readInputAndDuplicate();
        if (body.length() > MAX_BODY_SIZE) {
            log.trace("trim request from {} to {}", body.length(),
                      MAX_BODY_SIZE);
            body = body.substring(0, MAX_BODY_SIZE);
        }

        if (shouldLogRequest(requestWrapper)) {
            sendToSpan(KEY_REQUESTBODYRAW, body);
        }
    }

    private void logResponse(ContentCachingResponseWrapper responseWrapper) throws IOException {
        if (shouldLogResponsePayload(responseWrapper)) {
            log.debug("Response {}", new String(responseWrapper.getContentAsByteArray()));
        }
        responseWrapper.copyBodyToResponse();
    }

    private boolean shouldLogRequest(RepeatableContentCachingRequestWrapper requestWrapper) {
        String method = requestWrapper.getMethod();
        boolean b = Stream.of("POST", "PUT", "PATCH")
            .anyMatch(m -> m.equalsIgnoreCase(method));
        log.trace("SHOULD LOG = {} - {}:{}", b, method, requestWrapper.getServletPath());
        return b;
    }

    private boolean shouldLogResponsePayload(ContentCachingResponseWrapper responseWrapper) {
        return false;
    }
}
