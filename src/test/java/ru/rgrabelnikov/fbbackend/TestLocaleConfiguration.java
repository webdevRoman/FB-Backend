package ru.rgrabelnikov.fbbackend;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestLocaleConfiguration {

    @Bean
    public WebProperties.LocaleResolver localeResolver() {
        return WebProperties.LocaleResolver.FIXED;
    }
}
