package com.example.demo.app.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    TelemetryClient telemetryClient;

    @GetMapping("/greetings")
    public String greetings() {
        // send event
        telemetryClient.trackEvent("URI /greeting is triggered");

        return "Hello World!";
    }
}