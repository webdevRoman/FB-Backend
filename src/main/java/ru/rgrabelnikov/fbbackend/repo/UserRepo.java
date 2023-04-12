package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.model.UserEntity;

import java.util.UUID;

public interface UserRepo extends ReactiveCrudRepository<UserEntity, UUID> {

    Mono<UserEntity> findByLogin(String login);
}