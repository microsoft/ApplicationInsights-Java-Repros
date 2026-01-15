# Application Insights Log4j2 Integration Fix Summary

## Issue Description
The Spring Boot application was not sending Log4j2 and SLF4J logs to Azure Application Insights, despite having the necessary OpenTelemetry dependencies and Log4j2 OpenTelemetryAppender configured.

## Root Causes Identified

### 1. Missing OpenTelemetryAppender Installation
The primary issue was that the OpenTelemetry SDK instance was created but never connected to the Log4j2 OpenTelemetryAppender. Without this connection, the appender had no OpenTelemetry instance to use for exporting logs.

### 2. Missing Package Scanning Configuration
The Log4j2 configuration file did not specify the package containing the OpenTelemetryAppender, preventing Log4j2 from discovering the custom appender.

### 3. No Circular Logging Prevention
The configuration lacked protection against circular logging, where the Azure Monitor exporter's own logs could be sent back through the OpenTelemetryAppender, potentially causing infinite loops or performance issues.

## Fixes Applied

### Fix 1: Install OpenTelemetryAppender in Configuration Class

**File:** `src/main/java/com/example/TelemetryLogToAzureConfig.java`

**Changes:**
1. Added import statement:
   ```java
   import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
   ```

2. Modified the `openTelemetrywithConnectionString()` method to install the appender:
   ```java
   @Bean
   public OpenTelemetrySdk openTelemetrywithConnectionString() {
       AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
       AzureMonitorAutoConfigure.customize(sdkBuilder, connectionString);
       OpenTelemetrySdk sdk = sdkBuilder.build().getOpenTelemetrySdk();
       
       // Install the OpenTelemetry instance to the Log4j2 appender
       OpenTelemetryAppender.install(sdk);
       
       return sdk;
   }
   ```

**Why this fixes the issue:**
The `OpenTelemetryAppender.install(sdk)` call registers the OpenTelemetry SDK instance globally, making it available to the Log4j2 OpenTelemetryAppender. This bridges the gap between Log4j2 logging and OpenTelemetry's telemetry export pipeline.

### Fix 2: Update Log4j2 Configuration

**File:** `src/main/resources/log4j2.xml`

**Changes:**

1. **Added packages attribute** to the Configuration element:
   ```xml
   <Configuration status="WARN" packages="io.opentelemetry.instrumentation.log4j.appender.v2_17">
   ```
   This tells Log4j2 where to find the OpenTelemetryAppender plugin.

2. **Changed status from INFO to WARN**:
   Reduces Log4j2's internal logging noise.

3. **Added circular logging prevention**:
   ```xml
   <Loggers>
       <Root level="INFO">
           <AppenderRef ref="OpenTelemetryAppender" />
           <AppenderRef ref="ConsoleAppender" />
       </Root>
       
       <!-- Prevent circular logging: exclude Azure Monitor exporter logs from being sent back through OpenTelemetryAppender -->
       <Logger name="com.azure.monitor.opentelemetry.exporter" level="DEBUG" additivity="false">
           <AppenderRef ref="ConsoleAppender" />
       </Logger>
   </Loggers>
   ```

**Why this fixes the issue:**
- The `packages` attribute allows Log4j2 to discover the custom OpenTelemetryAppender plugin
- The special logger configuration prevents Azure Monitor exporter's internal logs from being exported again, avoiding circular logging and potential performance issues

## Complete File Changes

### TelemetryLogToAzureConfig.java (BEFORE)
```java
package com.example;

import com.azure.monitor.opentelemetry.autoconfigure.AzureMonitorAutoConfigure;
import io.opentelemetry.api.trace.Span;
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
        return sdkBuilder.build().getOpenTelemetrySdk();
    }
    
    // ... rest of the class
}
```

### TelemetryLogToAzureConfig.java (AFTER)
```java
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
    
    // ... rest of the class
}
```

### log4j2.xml (BEFORE)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="INFO">
    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p --- %C : %msg%n"/>
        </Console>
        <OpenTelemetry name="OpenTelemetryAppender" captureMapMessageAttributes="true" captureExperimentalAttributes="true" captureContextDataAttributes="*"/>
    </Appenders>
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="OpenTelemetryAppender" />
            <AppenderRef ref="ConsoleAppender" />
        </Root>
    </Loggers>
</Configuration>
```

### log4j2.xml (AFTER)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" packages="io.opentelemetry.instrumentation.log4j.appender.v2_17">
    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p --- %C : %msg%n"/>
        </Console>
        <OpenTelemetry name="OpenTelemetryAppender" captureMapMessageAttributes="true" captureExperimentalAttributes="true" captureContextDataAttributes="*"/>
    </Appenders>
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="OpenTelemetryAppender" />
            <AppenderRef ref="ConsoleAppender" />
        </Root>
        <!-- Prevent circular logging: exclude Azure Monitor exporter logs from being sent back through OpenTelemetryAppender -->
        <Logger name="com.azure.monitor.opentelemetry.exporter" level="DEBUG" additivity="false">
            <AppenderRef ref="ConsoleAppender" />
        </Logger>
    </Loggers>
</Configuration>
```

## How to Apply These Fixes

### Step 1: Update TelemetryLogToAzureConfig.java
1. Open `src/main/java/com/example/TelemetryLogToAzureConfig.java`
2. Add the import at the top of the file:
   ```java
   import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
   ```
3. Modify the `openTelemetrywithConnectionString()` method to store the SDK instance and install it:
   ```java
   @Bean
   public OpenTelemetrySdk openTelemetrywithConnectionString() {
       AutoConfiguredOpenTelemetrySdkBuilder sdkBuilder = AutoConfiguredOpenTelemetrySdk.builder();
       AzureMonitorAutoConfigure.customize(sdkBuilder, connectionString);
       OpenTelemetrySdk sdk = sdkBuilder.build().getOpenTelemetrySdk();
       OpenTelemetryAppender.install(sdk);
       return sdk;
   }
   ```

### Step 2: Update log4j2.xml
1. Open `src/main/resources/log4j2.xml`
2. Update the `<Configuration>` opening tag:
   - Change `status="INFO"` to `status="WARN"`
   - Add `packages="io.opentelemetry.instrumentation.log4j.appender.v2_17"`
3. Add the circular logging prevention logger before the closing `</Loggers>` tag:
   ```xml
   <Logger name="com.azure.monitor.opentelemetry.exporter" level="DEBUG" additivity="false">
       <AppenderRef ref="ConsoleAppender" />
   </Logger>
   ```

### Step 3: Rebuild and Test
1. Clean and rebuild the application:
   ```bash
   mvn clean package
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```
3. Test the logging endpoint:
   ```bash
   curl http://localhost:8090/say-hello?name=Test
   ```
4. Verify logs appear in Azure Application Insights (may take 1-2 minutes for telemetry to appear)

## Expected Results

After applying these fixes:
- ✅ Log4j2 logs will be captured by the OpenTelemetryAppender
- ✅ SLF4J logs (using Log4j2 backend) will be captured
- ✅ Logs will be exported to Azure Application Insights
- ✅ No circular logging issues
- ✅ Console logging continues to work as before

## Technical Explanation

The OpenTelemetry Log4j2 appender works by:
1. Capturing log events from Log4j2
2. Converting them to OpenTelemetry LogRecord format
3. Passing them to the OpenTelemetry SDK's LogRecordProcessor
4. The SDK batches and exports logs to the configured exporter (Azure Monitor)

The key requirement is that the appender must know which OpenTelemetry SDK instance to use. The `OpenTelemetryAppender.install(sdk)` method registers the SDK globally, allowing the appender (configured in log4j2.xml) to find and use it.

Without this installation step, the appender has no SDK to send logs to, resulting in logs being written to console but not exported to Application Insights.

## Reference: Working Example Comparison

The "working" application demonstrates the correct pattern in a standalone Java application:
```java
OpenTelemetry openTelemetry = initOpenTelemetry();
OpenTelemetryAppender.install(openTelemetry);  // Critical step
```

The key insight is that this pattern applies equally to Spring Boot applications - the SDK must be installed to the appender regardless of whether it's managed as a Spring Bean or created directly.

---

**Document Date:** January 15, 2026  
**Application:** logs-to-azure Spring Boot Application  
**OpenTelemetry Version:** 1.56.0  
**Azure Monitor Exporter Version:** 1.4.0  
**Log4j2 Version:** 2.25.3
