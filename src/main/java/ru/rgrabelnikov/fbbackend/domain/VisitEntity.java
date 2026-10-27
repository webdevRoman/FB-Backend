package ru.rgrabelnikov.fbbackend.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("visit")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VisitEntity extends BaseEntity {
}
