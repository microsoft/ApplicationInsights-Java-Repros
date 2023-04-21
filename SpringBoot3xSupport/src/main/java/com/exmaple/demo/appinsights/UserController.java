package com.exmaple.demo.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    private final TelemetryClient telemetryClient = new TelemetryClient();

    @GetMapping("/greetings")
    public String greetings() {
        // send event
        telemetryClient.trackEvent("URI /greeting is triggered");

        return "Hello World!";
    }
}