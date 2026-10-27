package ru.rgrabelnikov.fbbackend.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.rgrabelnikov.fbbackend.domain.VisitEntity;

import java.util.UUID;

public interface VisitRepo extends R2dbcRepository<VisitEntity, UUID> {
}
