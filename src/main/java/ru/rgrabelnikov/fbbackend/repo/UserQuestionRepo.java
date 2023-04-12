package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.rgrabelnikov.fbbackend.model.UserQuestionEntity;

import java.util.UUID;

public interface UserQuestionRepo extends ReactiveCrudRepository<UserQuestionEntity, UUID> {

}