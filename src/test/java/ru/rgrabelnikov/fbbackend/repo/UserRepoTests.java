package ru.rgrabelnikov.fbbackend.repo;

import com.github.f4b6a3.uuid.UuidCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.rgrabelnikov.fbbackend.TestcontainersInitializer;
import ru.rgrabelnikov.fbbackend.UserFactory;
import ru.rgrabelnikov.fbbackend.dto.UserRegistrationDto;
import ru.rgrabelnikov.fbbackend.model.Role;
import ru.rgrabelnikov.fbbackend.model.UserEntity;
import ru.rgrabelnikov.fbbackend.model.UserQuestionEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataR2dbcTest
public class UserRepoTests extends TestcontainersInitializer {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserQuestionRepo userQuestionRepo;

    @Test
    @DisplayName("Сохранение пользователя")
    public void saveUser() {
        UserRegistrationDto userRegistrationDto = UserFactory.createUserRegistrationDto();
        UUID userQuestionId = UuidCreator.getTimeOrdered();
        UUID userId = UuidCreator.getTimeOrdered();

        Mono<UserEntity> setup = userRepo.deleteAll()
                .then(userQuestionRepo.deleteAll())
                .hasElement()
                .map(__ -> {
                    UserQuestionEntity userQuestionEntity = new UserQuestionEntity();
                    userQuestionEntity.setId(userQuestionId);
                    userQuestionEntity.setQuestion("question");
                    return userQuestionEntity;
                })
                .flatMap(userQuestionEntity -> userQuestionRepo.save(userQuestionEntity))
                .map(userQuestionEntity -> {
                    userRegistrationDto.setQuestionId(userQuestionEntity.getId().toString());
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(userId);
                    userEntity.setLogin(userRegistrationDto.getLogin());
                    userEntity.setPassword(userRegistrationDto.getPassword());
                    userEntity.setRole(Role.USER);
                    userEntity.setQuestionId(UUID.fromString(userRegistrationDto.getQuestionId()));
                    userEntity.setQuestionAnswer(userRegistrationDto.getAnswer());
                    return userEntity;
                })
                .flatMap(userEntity -> userRepo.save(userEntity));
        Mono<UserEntity> find = userRepo.findById(userId);
        Mono<UserEntity> composite = setup
                .then(find);

        StepVerifier
                .create(composite)
                .consumeNextWith(user -> {
                    assertEquals(user.getId(), userId);
                    assertEquals(user.getLogin(), userRegistrationDto.getLogin());
                    assertEquals(user.getPassword(), userRegistrationDto.getPassword());
                    assertEquals(user.getRole(), Role.USER);
                    assertEquals(user.getQuestionId(), userQuestionId);
                    assertEquals(user.getQuestionAnswer(), userRegistrationDto.getAnswer());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск пользователя по логину")
    public void findUserByLogin() {
        UserRegistrationDto userRegistrationDto = UserFactory.createUserRegistrationDto();

        Mono<UserEntity> setup = userRepo.deleteAll()
                .then(userQuestionRepo.deleteAll())
                .hasElement()
                .map(__ -> {
                    UserQuestionEntity userQuestionEntity = new UserQuestionEntity();
                    userQuestionEntity.setId();
                    userQuestionEntity.setQuestion("question");
                    return userQuestionEntity;
                })
                .flatMap(userQuestionEntity -> userQuestionRepo.save(userQuestionEntity))
                .map(userQuestionEntity -> {
                    userRegistrationDto.setQuestionId(userQuestionEntity.getId().toString());
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId();
                    userEntity.setLogin(userRegistrationDto.getLogin());
                    userEntity.setPassword(userRegistrationDto.getPassword());
                    userEntity.setRole(Role.USER);
                    userEntity.setQuestionId(UUID.fromString(userRegistrationDto.getQuestionId()));
                    userEntity.setQuestionAnswer(userRegistrationDto.getAnswer());
                    return userEntity;
                })
                .flatMap(userEntity -> userRepo.save(userEntity));
        Mono<UserEntity> find = userRepo.findByLogin(userRegistrationDto.getLogin());
        Mono<UserEntity> composite = setup
                .then(find);

        StepVerifier
                .create(composite)
                .consumeNextWith(user -> {
                    assertNotNull(user);
                    assertEquals(user.getLogin(), userRegistrationDto.getLogin());
                })
                .verifyComplete();
    }
}