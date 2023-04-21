package com.exmaple.demo.appinsights;

import com.microsoft.applicationinsights.attach.ApplicationInsights;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DemoAppInsightsApplication {

	public static void main(String[] args) {
		ApplicationInsights.attach();
		SpringApplication.run(DemoAppInsightsApplication.class, args);
	}
}