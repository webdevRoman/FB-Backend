package ru.rgrabelnikov.fbbackend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

public class IdEntity implements Persistable<UUID> {

    @Getter
    @Id
    @NotNull
    private UUID id;

    @Transient
    private boolean isNew;

    public UUID setId() {
        this.id = UuidCreator.getTimeOrdered();
        isNew = true;
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
        isNew = true;
    }

    @Transient
    @Override
    public boolean isNew() {
        return isNew;
    }
}