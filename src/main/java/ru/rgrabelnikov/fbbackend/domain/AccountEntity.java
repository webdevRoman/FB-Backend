package ru.rgrabelnikov.fbbackend.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("account")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends BaseEntity {

    private String name;

    private UUID iconId;

    private UUID userId;
}
