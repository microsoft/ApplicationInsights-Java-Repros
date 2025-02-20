The code is prepared for issue [ApplicationInsights-Java#4095](https://github.com/microsoft/ApplicationInsights-Java/issues/4095).

1. Package an application.
   ```bash
   mvn clean package
   ```

1. Download Application Insights Java Agent.
   ```bash
   curl -Lo applicationinsights-agent.jar https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.7.0/applicationinsights-agent-3.7.0.jar
   ```

1. Set environment variables.
   ```bash
   export APPLICATIONINSIGHTS_CONNECTION_STRING="..."
   export APPLICATIONINSIGHTS_INSTRUMENTATION_LOGGING_LEVEL="OFF"
   ```

1. Run the application.
   ```bash
   java -javaagent:applicationinsights-agent.jar -jar target/opentelemetry-demo-1.0-SNAPSHOT.jar
   ```
