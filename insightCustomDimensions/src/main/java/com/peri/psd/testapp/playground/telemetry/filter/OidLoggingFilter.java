package com.peri.psd.testapp.playground.telemetry.filter;

import com.nimbusds.jwt.SignedJWT;
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
import java.text.ParseException;

@Component
@ConditionalOnProperty(name = "psd.azure.insight.filter.oid-filter", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
@Order(SecurityProperties.DEFAULT_FILTER_ORDER + 110)
public class OidLoggingFilter extends AbstractLoggingFilter {

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

        String authHeader = requestWrapper.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7); // Token without "Bearer "

            try {
                SignedJWT parsed = SignedJWT.parse(jwt);
                String oid = parsed.getJWTClaimsSet()
                    .getClaim("oid")
                    .toString();
                sendToSpan(KEY_CALLER, oid);

            } catch (ParseException e) {
                log.error("JWT parse error", e);
            }

        } else {
            log.warn("Request has no SecurityContext and no Bearer Token");
        }

    }

}
