package com.smartlogi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartLogiV2Application {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("SMTP-USERNAME", dotenv.get("SMTP-USERNAME"));
        System.setProperty("SMTP-PASSWORD", dotenv.get("SMTP-PASSWORD"));
        SpringApplication.run(SmartLogiV2Application.class, args);
    }
}
