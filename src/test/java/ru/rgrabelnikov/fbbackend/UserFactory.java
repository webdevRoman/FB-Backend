package ru.rgrabelnikov.fbbackend;

import com.github.f4b6a3.uuid.UuidCreator;
import ru.rgrabelnikov.fbbackend.dto.UserRegistrationDto;
import ru.rgrabelnikov.fbbackend.model.Role;
import ru.rgrabelnikov.fbbackend.model.UserEntity;

public class UserFactory {

    public final static String LOGIN = "login";
    public final static String PASSWORD = "password";
    public final static String ANSWER = "answer";

    public static UserRegistrationDto createUserRegistrationDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setLogin(LOGIN);
        dto.setPassword(PASSWORD);
        dto.setQuestionId(UuidCreator.getTimeOrdered().toString());
        dto.setAnswer(ANSWER);
        return dto;
    }

    public static UserEntity createUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId();
        entity.setLogin(LOGIN);
        entity.setPassword(PASSWORD);
        entity.setRole(Role.USER);
        entity.setQuestionId(UuidCreator.getTimeOrdered());
        entity.setQuestionAnswer(ANSWER);
        return entity;
    }
}