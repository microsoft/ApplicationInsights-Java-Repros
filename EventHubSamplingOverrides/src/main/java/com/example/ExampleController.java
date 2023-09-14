// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example;

import com.azure.identity.AzureAuthorityHosts;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.*;
import org.apache.qpid.proton.message.Message;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Sinks;

@RestController
public class ExampleController {

  private static final String NAMESPACE = "<your event hub namespace>";
  private static final String EVENTHUB_NAME = "<your event hub name>";
  private static final String TOPIC = "<your topic>";

  @RequestMapping("/")
  public String root() {

    Logger log = LoggerFactory.getLogger(ExampleController.class);

    // create a token using the default Azure credential
    DefaultAzureCredential credential = new DefaultAzureCredentialBuilder()
        .authorityHost(AzureAuthorityHosts.AZURE_PUBLIC_CLOUD)
        .build();

    // create a producer client
    EventHubProducerClient producer = new EventHubClientBuilder()
        .fullyQualifiedNamespace(NAMESPACE)
        .eventHubName(EVENTHUB_NAME)
        .credential(credential)
        .buildProducerClient();
    EventData event = new EventData(TOPIC);

    EventDataBatch dataBatch = producer.createBatch();
    if(dataBatch.tryAdd(event)) {
      log.debug("EventData added to data batch.");
    } else {
      log.error("Error adding event {} to data batch", event.getBodyAsString());
    }
    producer.send(dataBatch);

    producer.close();

    return "OK";
  }

  private Sinks.Many<Message> many;

  @RequestMapping("/messages")
  public ResponseEntity<String> sendMessage(@RequestParam String message) {
    many.emitNext((Message) MessageBuilder.withPayload(message).build(),
        Sinks.EmitFailureHandler.FAIL_FAST);
    return ResponseEntity.ok("The following message was sent: " + message);
  }
}