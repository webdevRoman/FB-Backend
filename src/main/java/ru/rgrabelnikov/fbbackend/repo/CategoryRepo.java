package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.rgrabelnikov.fbbackend.domain.CategoryEntity;

import java.util.List;
import java.util.UUID;

public interface CategoryRepo extends R2dbcRepository<CategoryEntity, UUID> {

    Flux<CategoryEntity> findByUserIdAndIncome(UUID userId, Boolean income);

    Flux<CategoryEntity> findByIdAndUserId(UUID id, UUID userId);

    Flux<CategoryEntity> findByIdIn(List<UUID> id);

    Flux<CategoryEntity> findByNameIn(List<String> name);
}
