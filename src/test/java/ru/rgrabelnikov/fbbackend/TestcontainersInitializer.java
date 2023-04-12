package ru.rgrabelnikov.fbbackend;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ContextConfiguration(initializers = {TestcontainersInitializer.Initializer.class})
public class TestcontainersInitializer {

    private static final String DATABASE_NAME = "test-db";
    private static final String DATABASE_USERNAME = "test-user";
    private static final String DATABASE_PASSWORD = "test-password";

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withReuse(true)
            .withDatabaseName(DATABASE_NAME)
            .withUsername(DATABASE_USERNAME)
            .withPassword(DATABASE_PASSWORD);

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "CONTAINER.USERNAME=" + postgreSQLContainer.getUsername(),
                    "CONTAINER.PASSWORD=" + postgreSQLContainer.getPassword(),
                    "CONTAINER.JDBC_URL=" + postgreSQLContainer.getJdbcUrl(),
                    "CONTAINER.R2BC_URL=" + String.format("r2dbc:pool:postgresql://%s:%d/%s",
                            postgreSQLContainer.getHost(),
                            postgreSQLContainer.getFirstMappedPort(),
                            postgreSQLContainer.getDatabaseName())
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}