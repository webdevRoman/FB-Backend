package ru.rgrabelnikov.fbbackend.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import static org.assertj.core.api.Assertions.assertThat;

class ChaosHealthIndicatorTest {

    @Test
    void shouldReturnUpWhenChaosDisabled() {
        ChaosHealthIndicator indicator = new ChaosHealthIndicator(new ChaosProperties(false));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void shouldReturnDownWhenChaosEnabled() {
        ChaosHealthIndicator indicator = new ChaosHealthIndicator(new ChaosProperties(true));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("reason");
    }
}
