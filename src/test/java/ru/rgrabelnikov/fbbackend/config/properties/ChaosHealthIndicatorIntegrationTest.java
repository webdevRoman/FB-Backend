package ru.rgrabelnikov.fbbackend.config.properties;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import ru.rgrabelnikov.fbbackend.AbstractIntegrationTest;

@TestPropertySource(properties = "fb.chaos.enabled=true")
class ChaosHealthIndicatorIntegrationTest extends AbstractIntegrationTest {

    private static final String REDIS_IMAGE = "redis:8.10.2-alpine";

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void healthReturnsDownWhenChaosEnabled() {
        webClient
                .get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.status").isEqualTo("DOWN");
    }
}
