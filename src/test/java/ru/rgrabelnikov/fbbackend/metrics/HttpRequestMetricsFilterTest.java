package ru.rgrabelnikov.fbbackend.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestMetricsFilterTest {

    private MeterRegistry meterRegistry;
    private HttpRequestMetricsFilter filter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        filter = new HttpRequestMetricsFilter(meterRegistry);
    }

    @Test
    void shouldIncrementCounterForVisitPath() {
        ServerWebExchange exchange = exchange("/api/v1/finance-balance/visit");

        StepVerifier.create(filter.filter(exchange, chain(HttpStatus.OK))).verifyComplete();

        assertThat(meterRegistry.find("http_requests_total").tag("status_code", "200").counter())
                .isNotNull()
                .extracting(Counter::count).isEqualTo(1.0);
    }

    @Test
    void shouldIncrementCounterForHealthPath() {
        ServerWebExchange exchange = exchange("/actuator/health");

        StepVerifier.create(filter.filter(exchange, chain(HttpStatus.OK))).verifyComplete();

        assertThat(meterRegistry.find("http_requests_total").tag("status_code", "200").counter())
                .isNotNull()
                .extracting(Counter::count).isEqualTo(1.0);
    }

    @Test
    void shouldNotIncrementCounterForPrometheusPaths() {
        ServerWebExchange exchange = exchange("/api/v1/finance-balance/actuator/prometheus");

        StepVerifier.create(filter.filter(exchange, chain(HttpStatus.OK))).verifyComplete();

        assertThat(meterRegistry.find("http_requests_total").counter()).isNull();
    }

    @Test
    void shouldCountStatusCodesSeparately() {
        StepVerifier.create(filter.filter(exchange("/api/v1/finance-balance/visit"),
                chain(HttpStatus.OK))).verifyComplete();
        StepVerifier.create(filter.filter(exchange("/api/v1/finance-balance/visit"),
                chain(HttpStatus.INTERNAL_SERVER_ERROR))).verifyComplete();
        StepVerifier.create(filter.filter(exchange("/actuator/health"),
                chain(HttpStatus.NOT_FOUND))).verifyComplete();

        assertThat(meterRegistry.find("http_requests_total").tag("status_code", "200").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.find("http_requests_total").tag("status_code", "500").counter().count()).isEqualTo(1.0);
        assertThat(meterRegistry.find("http_requests_total").tag("status_code", "404").counter().count()).isEqualTo(1.0);
    }

    @Test
    void shouldExposeMetricInPrometheusFormat() {
        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        HttpRequestMetricsFilter prometheusFilter = new HttpRequestMetricsFilter(prometheusRegistry);

        StepVerifier.create(prometheusFilter.filter(exchange("/api/v1/finance-balance/visit"),
                chain(HttpStatus.OK))).verifyComplete();

        assertThat(prometheusRegistry.scrape()).contains("http_requests_total{status_code=\"200\"} 1.0");
    }

    private ServerWebExchange exchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }

    private WebFilterChain chain(HttpStatus status) {
        return exchange -> {
            exchange.getResponse().setStatusCode(status);
            return exchange.getResponse().setComplete();
        };
    }
}
