package ru.rgrabelnikov.fbbackend.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import ru.rgrabelnikov.fbbackend.AbstractIntegrationTest;
import ru.rgrabelnikov.fbbackend.dto.visit.VisitCountDto;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitsControllerTests extends AbstractIntegrationTest {

    private static final String REDIS_IMAGE = "redis:8.10.2-alpine";

    private static final String VISITS_COUNT_KEY = "visits_count";

    private static final String CACHE_SOURCE = "cache";

    private static final String DB_SOURCE = "db";

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @AfterEach
    public void clearVisitsCountCache() {
        redisTemplate.delete(VISITS_COUNT_KEY).block();
    }

    @Test
    public void firstRequestCreatesVisitInDatabase() {
        webClient
                .get()
                .uri("/visit")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(VisitCountDto.class)
                .value(rs -> {
                    assertThat(rs.source()).isEqualTo(DB_SOURCE);
                    assertThat(rs.count()).isGreaterThan(0);
//                    assertThat(rs.lastId()).isNotNull();
                    assertThat(rs.lastId()).isNull();
                });
    }

    @Test
    public void secondRequestWithinTtlServedFromCache() {
        webClient
                .get()
                .uri("/visit")
                .exchange()
                .expectStatus()
                .isOk();

        webClient
                .get()
                .uri("/visit")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(VisitCountDto.class)
                .value(rs -> {
                    assertThat(rs.source()).isEqualTo(CACHE_SOURCE);
                    assertThat(rs.count()).isGreaterThan(0);
                    assertThat(rs.lastId()).isNull();
                });
    }
}
