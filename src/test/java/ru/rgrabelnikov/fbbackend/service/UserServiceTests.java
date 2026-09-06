//package ru.rgrabelnikov.fbbackend.service;
//
//import com.github.f4b6a3.uuid.UuidCreator;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import ru.rgrabelnikov.fbbackend.domain.UserEntity;
//import ru.rgrabelnikov.fbbackend.dto.security.ContextUser;
//import ru.rgrabelnikov.fbbackend.mapper.CommonMapperImpl;
//import ru.rgrabelnikov.fbbackend.mapper.UserMapperImpl;
//import ru.rgrabelnikov.fbbackend.repo.IconRepo;
//import ru.rgrabelnikov.fbbackend.repo.UserQuestionRepo;
//import ru.rgrabelnikov.fbbackend.repo.UserRepo;
//import ru.rgrabelnikov.fbbackend.service.impl.UserServiceBean;
//import ru.rgrabelnikov.fbbackend.testutil.UserFactory;
//import ru.rgrabelnikov.fbbackend.util.JwtProvider;
//
//import java.util.UUID;
//
//import static ru.rgrabelnikov.fbbackend.dto.security.Role.USER;
//import static ru.rgrabelnikov.fbbackend.testutil.UserFactory.LOGIN;
//
//@Import({UserServiceBean.class, UserMapperImpl.class, CommonMapperImpl.class})
//@ExtendWith(SpringExtension.class)
//public class UserServiceTests {
//
//    @Autowired
//    private UserService userService;
//
//    @MockitoBean
//    private UserRepo userRepo;
//
//    @MockitoBean
//    private UserQuestionRepo userQuestionRepo;
//
//    @MockitoBean
//    private IconRepo iconRepo;
//
//    @MockitoBean
//    private PasswordEncoder passwordEncoder;
//
//    @MockitoBean
//    private JwtProvider jwtProvider;
//
//    @Test
//    public void findByUsername() {
//        final UUID questionId = UuidCreator.getTimeOrdered();
//        final UserEntity entity = UserFactory.createUserEntity(questionId);
//
//        Mockito.when(userRepo.findByLogin(LOGIN)).thenReturn(Mono.just(entity));
//
//        StepVerifier
//                .create(userService.findByUsername(LOGIN))
//                .consumeNextWith(userDetails -> {
//                    final SoftAssertions assertions = new SoftAssertions();
//                    assertions.assertThat(userDetails).isInstanceOf(ContextUser.class);
//                    final ContextUser user = (ContextUser) userDetails;
//                    assertions.assertThat(user.getUsername()).isEqualTo(LOGIN);
//                    assertions.assertThat(user.isEnabled()).isTrue();
//                    assertions.assertThat(user.isAccountNonExpired()).isTrue();
//                    assertions.assertThat(user.isCredentialsNonExpired()).isTrue();
//                    assertions.assertThat(user.isAccountNonLocked()).isTrue();
//                    assertions.assertThat(user.getAuthorities()).isNotNull();
//                    assertions.assertThat(user.getAuthorities().size()).isEqualTo(1);
//                    assertions.assertThat(user.getAuthorities().stream().toList().get(0).getAuthority()).isEqualTo("ROLE_" + USER.name());
//                    assertions.assertAll();
//                })
//                .verifyComplete();
//    }
//
//    // TODO getUserQuestions
//
//    @Test
//    public void register() {
//        final UserRegistrationDto dto = UserFactory.createUserRegistrationDto();
//        final UserEntity entity = UserFactory.createUserEntity(dto.questionId());
//        final String tokenValue = "token";
//
//        Mockito.when(userRepo.findByLogin(LOGIN)).thenReturn(Mono.empty());
//        Mockito.when(userRepo.save(Mockito.any(UserEntity.class))).thenReturn(Mono.just(entity));
//        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
//        Mockito.when(passwordEncoder.encode(ANSWER)).thenReturn(ANSWER);
//        Mockito.when(jwtProvider.generateToken(entity)).thenReturn(tokenValue);
//
//        Mono<JwtDto> jwtMono = userService.register(dto);
//        StepVerifier
//                .create(jwtMono)
//                .consumeNextWith(token -> assertThat(token.jwt()).isNotBlank())
//                .consumeNextWith(token -> assertThat(token.jwt()).isEqualTo(tokenValue))
//                .verifyComplete();
//    }
//
//    @Test
//    public void register_notUnique() {
//        final UserRegistrationDto dto = UserFactory.createUserRegistrationDto();
//        final UserEntity entity = UserFactory.createUserEntity(dto.questionId());
//
//        Mockito.when(userRepo.findByLogin(LOGIN)).thenReturn(Mono.just(entity));
//
//        Mono<JwtDto> tokenMono = userService.register(dto);
//        StepVerifier
//                .create(tokenMono)
//                .consumeErrorWith(ex -> {
//                    final SoftAssertions assertions = new SoftAssertions();
//                    assertions.assertThat(ex).isInstanceOf(ConflictException.class);
//                    assertions.assertThat(ex.getMessage()).isEqualTo(format("Login %s is not unique", LOGIN));
//                    assertions.assertAll();
//                })
//                .verify();
//    }
//
//     TODO login
//
//     TODO
//    @Test
//    public void recoverPassword() {
//        final String newPassword = "newPassword";
//        final UserAuthDto dto = UserFactory.createUserAuthDto(newPassword);
//        final UserEntity entity = UserFactory.createUserEntity();
//        final String tokenValue = "token";
//
//        Mockito.when(userRepo.findByLogin(LOGIN)).thenReturn(Mono.just(entity));
//        Mockito.when(userRepo.save(Mockito.any(UserEntity.class))).thenReturn(Mono.just(entity));
//        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);
//        Mockito.when(jwtProvider.generateToken(entity)).thenReturn(tokenValue);
//
//        Mono<ResponseEntity<?>> tokenMono = userService.recoverPassword(dto);
//        StepVerifier
//                .create(tokenMono)
//                .consumeNextWith(token -> {
//                    assertInstanceOf(String.class, token.getBody());
//                    assertEquals(tokenValue, token.getBody());
//                })
//                .verifyComplete();
//    }
//}
