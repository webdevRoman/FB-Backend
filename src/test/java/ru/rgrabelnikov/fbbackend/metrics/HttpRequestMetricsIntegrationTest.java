package ru.rgrabelnikov.fbbackend.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import ru.rgrabelnikov.fbbackend.AbstractIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestMetricsIntegrationTest extends AbstractIntegrationTest {

    private static final String REDIS_IMAGE = "redis:8.10.2-alpine";

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private MeterRegistry meterRegistry;

    @AfterEach
    void resetCounters() {
        meterRegistry.forEachMeter(meter -> {
            if ("http_requests_total".equals(meter.getId().getName())) {
                meterRegistry.remove(meter);
            }
        });
    }

    @Test
    void visitEndpointIncrementsCounter() {
        webClient
                .get()
                .uri("/visit")
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(counterCount("200")).isEqualTo(1.0);
    }

    @Test
    void actuatorHealthEndpointIncrementsCounter() {
        webClient
                .get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(counterCount("200")).isEqualTo(1.0);
    }

    private double counterCount(String statusCode) {
        return meterRegistry.get("http_requests_total")
                .tag("status_code", statusCode)
                .counter()
                .count();
    }
}
