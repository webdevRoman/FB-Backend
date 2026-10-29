package ru.rgrabelnikov.fbbackend.config.properties;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChaosHealthIndicator implements HealthIndicator {

    private final ChaosProperties chaosProperties;

    @Override
    public Health health() {
        if (Boolean.TRUE.equals(chaosProperties.enabled())) {
            return Health.down()
                    .withDetail("reason", "Chaos mode is enabled")
                    .build();
        }
        return Health.up().build();
    }
}
