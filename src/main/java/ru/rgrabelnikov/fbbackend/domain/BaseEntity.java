package ru.rgrabelnikov.fbbackend.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter(PRIVATE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseEntity extends IdEntity {

    @Version
    private Integer version;

    @CreatedDate
    private LocalDateTime createdOn;

    @LastModifiedDate
    private LocalDateTime updatedOn;
}
