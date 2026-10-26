package ru.rgrabelnikov.fbbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.domain.VisitEntity;
import ru.rgrabelnikov.fbbackend.dto.ServerErrorDto;
import ru.rgrabelnikov.fbbackend.dto.visit.VisitCountDto;
import ru.rgrabelnikov.fbbackend.repo.VisitRepo;

import java.time.Duration;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/visit", produces = {"application/json"})
@ApiResponse(responseCode = "200", description = "Запрос успешно обработан")
@ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@Tag(name = "Visit", description = "Счётчик посещений")
public class VisitsController {

    private static final String VISITS_COUNT_KEY = "visits_count";

    private static final Duration VISITS_COUNT_TTL = Duration.ofSeconds(10);

    private static final String CACHE_SOURCE = "cache";

    private static final String DB_SOURCE = "db";

    private final VisitRepo repo;

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @GetMapping
    @Operation(summary = "Учёт посещения")
    public Mono<VisitCountDto> visit() {
        return getCachedCount()
                .map(count -> new VisitCountDto(count, CACHE_SOURCE, null))
                .switchIfEmpty(Mono.defer(this::registerVisit));
    }

    private Mono<Long> getCachedCount() {
        return redisTemplate.opsForValue()
                .get(VISITS_COUNT_KEY)
                .map(Long::parseLong)
                .onErrorResume(error -> {
                    log.warn("Failed to get '{}' from Redis", VISITS_COUNT_KEY, error);
                    return Mono.empty();
                });
    }

    private Mono<VisitCountDto> registerVisit() {
        return repo.save(newVisit())
                .flatMap(visit -> repo.count()
                        .flatMap(count -> cacheCount(count)
                                .thenReturn(new VisitCountDto(count, DB_SOURCE, visit.getId()))));
    }

    private Mono<Boolean> cacheCount(final long count) {
        return redisTemplate.opsForValue()
                .set(VISITS_COUNT_KEY, Long.toString(count), VISITS_COUNT_TTL)
                .onErrorResume(error -> {
                    log.warn("Failed to put '{}' to Redis", VISITS_COUNT_KEY, error);
                    return Mono.just(false);
                });
    }

    private VisitEntity newVisit() {
        VisitEntity visit = new VisitEntity();
        visit.setId();
        return visit;
    }
}
