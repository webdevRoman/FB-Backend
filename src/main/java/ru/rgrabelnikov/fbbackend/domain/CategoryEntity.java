package ru.rgrabelnikov.fbbackend.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("category")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CategoryEntity extends BaseEntity {

    private Boolean income;

    private String name;

    private UUID userId;

    private UUID iconId;

    private UUID parentId;
}
