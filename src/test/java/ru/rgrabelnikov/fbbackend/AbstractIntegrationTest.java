package ru.rgrabelnikov.fbbackend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.TimeZone;

import static ru.rgrabelnikov.fbbackend.testutil.TestUtils.getAsString;

@SpringBootTest
@Testcontainers
@ActiveProfiles("TEST")
@EnableAutoConfiguration
@AutoConfigureWebTestClient
@Import({TestLocaleConfiguration.class})
public abstract class AbstractIntegrationTest {

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    private DatabaseClient databaseClient;

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.3-alpine")
            .withDatabaseName("fb-db")
            .withUsername("fb")
            .withPassword("fb-pwd");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> String.format("r2dbc:postgresql://%s:%d/%s",
                postgres.getHost(), postgres.getFirstMappedPort(), postgres.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.liquibase.url", () -> String.format("jdbc:postgresql://%s:%d/%s",
                postgres.getHost(), postgres.getFirstMappedPort(), postgres.getDatabaseName()));
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }

    @BeforeAll
    public static void beforeAll() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @AfterEach
    public void afterEach() {
        final String cleanDataScript = getAsString("db/clean_data.sql");
        final Flux<Map<String, Object>> cleanResult = databaseClient.sql(cleanDataScript).fetch().all();
        StepVerifier.create(cleanResult).verifyComplete();
    }
}
