package com.example.issue2808;

import com.azure.core.tracing.opentelemetry.OpenTelemetryTracingOptions;
import com.azure.core.util.ClientOptions;
import com.azure.core.util.TracingOptions;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.SpanData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application {

    private static final String CONNECTION_STRING = System.getenv("SERVICE_BUS_CONNECTION_STRING");
    private static final String QUEUE_NAME = "test";

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        receiveMessages();
    }

    private static void receiveMessages() {

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(new SpanProcessor() {
                @Override
                public void onStart(Context context, ReadWriteSpan readWriteSpan) {
                }

                @Override
                public boolean isStartRequired() {
                    return false;
                }

                @Override
                public void onEnd(ReadableSpan readableSpan) {
                    SpanData spanData = readableSpan.toSpanData();
                    System.out.println(spanData.getKind());
                    System.out.println(spanData.getName());
                    long nanos = spanData.getEndEpochNanos() - spanData.getStartEpochNanos();
                    System.out.println(TimeUnit.NANOSECONDS.toMillis(nanos) + " milliseconds");
                }

                @Override
                public boolean isEndRequired() {
                    return true;
                }
            })
            .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();

        TracingOptions customTracingOptions = new OpenTelemetryTracingOptions()
            .setOpenTelemetry(openTelemetry);

        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
            .clientOptions(new ClientOptions().setTracingOptions(customTracingOptions))
            .connectionString(CONNECTION_STRING)
            .processor()
            .queueName(QUEUE_NAME)
            .maxConcurrentCalls(2)
            .processMessage(Application::processMessage)
            .processError(Application::processError)
            .buildProcessorClient();

        logger.info("Starting the processor");
        processorClient.start();

        sendMessage();
    }

    private static void sendMessage() {
        ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
            .connectionString(CONNECTION_STRING)
            .sender()
            .queueName(QUEUE_NAME)
            .buildClient();

        senderClient.sendMessage(new ServiceBusMessage("Hello, World!"));
        logger.info("Sent a single message to the queue: {}", QUEUE_NAME);

        senderClient.close();
    }

    private static void processMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage message = context.getMessage();
        logger.info("Processing message. Session: {}, Sequence #: {}. Contents: {}\n",
            message.getMessageId(), message.getSequenceNumber(), message.getBody());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processError(ServiceBusErrorContext context) {
        context.getException().printStackTrace();
    }
}
