package ru.rgrabelnikov.fbbackend.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Table("operation")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OperationEntity extends BaseEntity {

    private UUID accountId;

    private UUID categoryId;

    private BigDecimal amount;

    private LocalDate date;

    private String description;
}
