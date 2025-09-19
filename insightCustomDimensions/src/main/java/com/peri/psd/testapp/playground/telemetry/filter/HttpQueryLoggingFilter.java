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

import java.io.IOException;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(name = "psd.azure.insight.filter.query-filter", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
@Order(SecurityProperties.DEFAULT_FILTER_ORDER + 130)
public class HttpQueryLoggingFilter extends AbstractLoggingFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        RepeatableContentCachingRequestWrapper requestWrapper = new RepeatableContentCachingRequestWrapper(
            request);

        logQueryParameter(requestWrapper);

        filterChain.doFilter(request, response);

    }

    private void logQueryParameter(
        RepeatableContentCachingRequestWrapper requestWrapper) throws IOException {

        String queryAsString = requestWrapper.getQueryString();

        if (shouldLogQuery(requestWrapper)) {
            sendToSpan(KEY_REQUESTQUERY, queryAsString);
        }
    }

    private boolean shouldLogQuery(RepeatableContentCachingRequestWrapper requestWrapper) {
        String method = requestWrapper.getMethod();
        boolean b = Stream.of("GET", "POST", "PUT", "PATCH", "DELETE")
            .anyMatch(m -> m.equalsIgnoreCase(method));
        log.trace("SHOULD LOG = {} - {}:{}", b, method, requestWrapper.getServletPath());
        return b;
    }

}
