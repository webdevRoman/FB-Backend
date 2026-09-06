package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.rgrabelnikov.fbbackend.domain.OperationEntity;

import java.time.LocalDate;
import java.util.UUID;

public interface OperationRepo extends R2dbcRepository<OperationEntity, UUID> {

    Flux<OperationEntity> findByAccountIdAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(
            UUID accountId, LocalDate dateFrom, LocalDate dateTo
    );

    Flux<OperationEntity> findByAccountIdOrderByDate(UUID accountId);
}
