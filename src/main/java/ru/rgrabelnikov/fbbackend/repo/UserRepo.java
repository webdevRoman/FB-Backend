package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.domain.UserEntity;

import java.util.UUID;

public interface UserRepo extends R2dbcRepository<UserEntity, UUID> {

    Mono<UserEntity> findByLogin(String login);
}
