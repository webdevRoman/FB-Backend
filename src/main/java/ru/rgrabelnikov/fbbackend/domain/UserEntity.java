package ru.rgrabelnikov.fbbackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;
import ru.rgrabelnikov.fbbackend.dto.security.Role;

import java.util.UUID;

@Data
@Table("usr")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends BaseEntity {

    private String login;

    @JsonIgnore
    private String password;

    private Role role;

    private UUID questionId;

    private String questionAnswer;
}
