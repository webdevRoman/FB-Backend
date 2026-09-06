package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.rgrabelnikov.fbbackend.domain.AccountEntity;

import java.util.UUID;

public interface AccountRepo extends R2dbcRepository<AccountEntity, UUID> {

    Flux<AccountEntity> findByUserId(UUID userId);

    Flux<AccountEntity> findByIdAndUserId(UUID id, UUID userId);
}
