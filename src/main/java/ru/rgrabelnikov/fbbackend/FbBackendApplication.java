package ru.rgrabelnikov.fbbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FbBackendApplication {

    static void main(String[] args) {
        SpringApplication.run(FbBackendApplication.class, args);
    }
}
