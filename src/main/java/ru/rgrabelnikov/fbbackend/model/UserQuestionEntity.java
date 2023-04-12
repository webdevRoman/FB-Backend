package ru.rgrabelnikov.fbbackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("user_question")
public class UserQuestionEntity extends IdEntity {

    @NotBlank
    private String question;
}