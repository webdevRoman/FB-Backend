package ru.rgrabelnikov.fbbackend.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Value("${spring.profiles.active:UNKNOWN}")
    private String activeProfile;

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        final boolean activeProfileLocal = "LOCAL".equalsIgnoreCase(activeProfile);
        final String[] permittedPaths = Stream.of(
                        "/users/login",
                        "/users/registration/meta",
                        "/users/registration",
                        "/users/recovery",
                        activeProfileLocal ? "/swagger-ui/**" : null,
                        activeProfileLocal ? "/v3/api-docs/**" : null
                )
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new);

        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .exceptionHandling(spec -> spec
                        .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(
                                () -> swe.getResponse().setStatusCode(UNAUTHORIZED)
                        ))
                        .accessDeniedHandler((swe, e) -> Mono.fromRunnable(
                                () -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)
                        )))
                .authorizeExchange(spec -> spec
                        .pathMatchers(permittedPaths).permitAll()
                        .anyExchange().authenticated())
                .build();
    }
}
