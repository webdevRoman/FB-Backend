package ru.rgrabelnikov.fbbackend.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("icon")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IconEntity extends BaseEntity {

    private UUID userId;

    private byte[] icon;

    private String tooltip;
}
