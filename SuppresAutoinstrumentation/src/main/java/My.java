import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.opentelemetry.context.Scope;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;

import io.opentelemetry.api.GlobalOpenTelemetry;

public class My {
    private static final Logger logger = LogManager.getLogger("root");
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("my-project");

    public static void main(String[] args) throws InterruptedException {
        logger.warn("Hello Azure");

        Span span = tracer.spanBuilder("My-span").startSpan();

        try (Scope scope = span.makeCurrent()) {
            Thread.sleep(1000L);   // Work simulation
            logger.info("Log from a trace");
        }
        finally {
            span.end();
        }
        Thread.sleep(10000);
    }
}