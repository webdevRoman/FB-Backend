package ru.rgrabelnikov.fbbackend.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class HttpRequestMetricsFilter implements WebFilter {

    private static final String METRIC_NAME = "http_requests_total";
    private static final String STATUS_CODE_TAG = "status_code";

    private static final Set<String> UNMONITORED_PATHS = Set.of("/actuator/prometheus");

    private final MeterRegistry meterRegistry;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (UNMONITORED_PATHS.stream().anyMatch(it -> exchange.getRequest().getURI().getPath().endsWith(it))) {
            return chain.filter(exchange);
        }

        exchange.getResponse().beforeCommit(() -> Mono.fromRunnable(() -> {
            HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
            int statusValue = statusCode != null ? statusCode.value() : HttpStatus.OK.value();
            log.debug("Incrementing metric for request {}, status {}", exchange.getRequest().getURI(), statusValue);
            Counter.builder(METRIC_NAME)
                    .tag(STATUS_CODE_TAG, String.valueOf(statusValue))
                    .register(meterRegistry)
                    .increment();
        }));

        return chain.filter(exchange);
    }

}
