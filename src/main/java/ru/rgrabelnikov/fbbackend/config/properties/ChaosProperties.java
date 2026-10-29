package ru.rgrabelnikov.fbbackend.config.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "fb.chaos")
public record ChaosProperties(
        @NotNull Boolean enabled
) {
}
