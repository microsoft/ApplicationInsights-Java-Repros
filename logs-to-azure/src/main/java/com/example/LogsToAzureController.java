package com.example;

import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.trace.Span;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 *  test
 */
@Slf4j
@RestController
@RequestMapping("/")
public class LogsToAzureController {
    private static final Log log1 = LogFactory.getLog(LogsToAzureController.class);
    private static final org.apache.logging.log4j.Logger log2 = LogManager.getLogger(LogsToAzureController.class);
    @Autowired
    private Logger otelLogger;
    @Autowired
    private Span span;


    @GetMapping("say-hello")
    public ResponseEntity<?> sayHello(@RequestParam(name = "name", required = false) String name)
            throws IOException {
        try{
            otelLogger.logRecordBuilder()
                    .setBody("OTel Logs test with io.opentelemetry.api.logs.Logger:")
                    .setSeverity(Severity.INFO)
                    .emit();
            log.info("log with Slf4j");
            log2.info("log with org.apache.logging.log4j");
            log1.info("log with org.apache.commons.logging.Log");
            span.setAttribute("endpoint", "/say-hello");
            String result = "Hello, Welcome to JAVA";
            return new ResponseEntity<>(result, HttpStatus.OK);
        } finally {
            span.end();
        }
    }
    }

