package ru.rgrabelnikov.fbbackend.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("user_question")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserQuestionEntity extends IdEntity {

    private String question;
}
