//package ru.rgrabelnikov.fbbackend.repo;
//
//import org.apache.commons.lang3.tuple.Pair;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import ru.rgrabelnikov.fbbackend.TestcontainersInitializer;
//import ru.rgrabelnikov.fbbackend.domain.UserEntity;
//
//import static ru.rgrabelnikov.fbbackend.dto.security.Role.USER;
//import static ru.rgrabelnikov.fbbackend.testutil.UserFactory.ANSWER;
//import static ru.rgrabelnikov.fbbackend.testutil.UserFactory.LOGIN;
//import static ru.rgrabelnikov.fbbackend.testutil.UserFactory.PASSWORD;
//import static ru.rgrabelnikov.fbbackend.testutil.UserFactory.createUserEntity;
//import static ru.rgrabelnikov.fbbackend.testutil.UserFactory.createUserQuestionEntity;
//
//@DataR2dbcTest
//public class UserRepoTests extends TestcontainersInitializer {
//
//    @Autowired
//    private UserRepo userRepo;
//
//    @Autowired
//    private UserQuestionRepo userQuestionRepo;
//
//    @Test
//    public void findByLogin() {
//        final Mono<UserEntity> setup =
//                userRepo.deleteAll()
//                        .then(userQuestionRepo.deleteAll())
//                        .then(userQuestionRepo.save(createUserQuestionEntity()))
//                        .map(userQuestionEntity -> createUserEntity(userQuestionEntity.getId()))
//                        .flatMap(source -> userRepo.save(source)
//                                .map(_ -> source));
//        final Mono<Pair<UserEntity, UserEntity>> find = setup
//                .flatMap(source -> userRepo.findByLogin(LOGIN)
//                        .map(found -> Pair.of(source, found)));
//
//        StepVerifier
//                .create(find)
//                .consumeNextWith(pair -> {
//                    final SoftAssertions assertions = new SoftAssertions();
//                    assertions.assertThat(pair.getRight().getId()).isEqualTo(pair.getLeft().getId());
//                    assertions.assertThat(pair.getRight().getLogin()).isEqualTo(LOGIN);
//                    assertions.assertThat(pair.getRight().getPassword()).isEqualTo(PASSWORD);
//                    assertions.assertThat(pair.getRight().getRole()).isEqualTo(USER);
//                    assertions.assertThat(pair.getRight().getQuestionId()).isEqualTo(pair.getLeft().getQuestionId());
//                    assertions.assertThat(pair.getRight().getQuestionAnswer()).isEqualTo(ANSWER);
//                    assertions.assertAll();
//                })
//                .verifyComplete();
//    }
//}
