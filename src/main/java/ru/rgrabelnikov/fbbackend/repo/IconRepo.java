package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;

import java.util.List;
import java.util.UUID;

public interface IconRepo extends R2dbcRepository<IconEntity, UUID> {

    Flux<IconEntity> findByUserId(UUID userId);

    Flux<IconEntity> findByIdAndUserId(UUID id, UUID userId);

    Flux<IconEntity> findByIdIn(List<UUID> id);
}
