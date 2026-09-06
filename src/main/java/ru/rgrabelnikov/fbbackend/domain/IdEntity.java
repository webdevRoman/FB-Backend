package ru.rgrabelnikov.fbbackend.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

public abstract class IdEntity implements Persistable<UUID> {

    @Id
    @Getter
    @NonNull
    private UUID id;

    @Transient
    private boolean isNew;

    public void setId() {
        this.id = UuidCreator.getTimeOrdered();
        isNew = true;
    }

    @Transient
    @Override
    public boolean isNew() {
        return isNew;
    }
}
