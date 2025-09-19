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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(name = "psd.azure.insight.filter.country-filter", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
@Order(SecurityProperties.DEFAULT_FILTER_ORDER + 120)
public class CountryLoggingFilter extends AbstractLoggingFilter {

    // https://petrepopescu.tech/2023/07/how-to-log-http-request-and-response-in-spring-boot/
    // https://www.linkedin.com/pulse/rest-api-monitoring-azure-application-insights-srinivas-anumala
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        RepeatableContentCachingRequestWrapper requestWrapper = new RepeatableContentCachingRequestWrapper(
            request);

        logRequest(requestWrapper);

        filterChain.doFilter(request, response);

    }

    private void logRequest(
        RepeatableContentCachingRequestWrapper requestWrapper) throws IOException {
        extractCurrentDbFromUri(requestWrapper.getRequestURI())
            .ifPresent(currentDb ->
                           sendToSpan(KEY_COUNTRY, currentDb)
            );
    }

    /**
     * @param uri i.e /api/v3/germany/foo or /germany/api/v3/foo
     * @return germany
     */
    public static Optional<String> extractCurrentDbFromUri(String uri) {
        if (uri == null || uri.trim()
            .isEmpty()) {
            return Optional.empty();
        }

        Pattern pattern = Pattern.compile("^/([^/]+)/api/v\\d+/.+|^/api/v\\d+/([^/]+)/.+",
                                          Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            // Group 1: db before /api/vXX, Group 2: db after /api/vXX
            return Optional.ofNullable(
                matcher.group(1) != null ? matcher.group(1) : matcher.group(2)
            );
        }
        return Optional.empty();
    }

}
