package ru.rgrabelnikov.fbbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("usr")
public class UserEntity extends IdEntity {

    @NotBlank
    private String login;

    @NotBlank
    @JsonIgnore
    private String password;

    @NotBlank
    private Role role;

    @NotBlank
    private UUID questionId;

    @NotBlank
    private String questionAnswer;
}