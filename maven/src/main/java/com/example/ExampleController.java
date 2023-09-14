// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example;

import com.azure.core.amqp.ProxyAuthenticationType;
import com.azure.core.amqp.ProxyOptions;
import com.azure.messaging.eventhubs.*;
import com.azure.core.amqp.AmqpTransportType;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Proxy;

@RestController
public class ExampleController {

    @Value("${eventhub.connection_string}")
    private String eventHubConnectionString;

    @Value("${eventhub.topic}")
    private String eventHubTopic;

    @GetMapping("/")
    public String root() {

        Logger log = LoggerFactory.getLogger(ExampleController.class);

        EventHubProducerClient producer = new EventHubClientBuilder().transportType(AmqpTransportType.AMQP_WEB_SOCKETS)
                .connectionString(this.eventHubConnectionString, this.eventHubTopic)
                .buildProducerClient();

        EventData event = new EventData("test");

        EventDataBatch dataBatch = producer.createBatch();
        if(dataBatch.tryAdd(event)) {
            log.debug("EventData added to data batch.");
        }
        else {
            log.error("Error adding event {} to data batch", event.getBodyAsString());
        }
        producer.send(dataBatch);

        producer.close();

        return "OK";
    }
}
