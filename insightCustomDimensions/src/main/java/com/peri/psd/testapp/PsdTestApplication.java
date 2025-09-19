package com.peri.psd.testapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.peri.psd.testapp"})
@EnableConfigurationProperties
public class PsdTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(PsdTestApplication.class, args);
    }

}
