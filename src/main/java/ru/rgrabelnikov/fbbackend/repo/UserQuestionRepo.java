package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.rgrabelnikov.fbbackend.domain.UserQuestionEntity;

import java.util.UUID;

public interface UserQuestionRepo extends R2dbcRepository<UserQuestionEntity, UUID> {
}
