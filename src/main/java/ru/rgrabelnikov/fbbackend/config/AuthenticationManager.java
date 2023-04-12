package ru.rgrabelnikov.fbbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.mapper.UserMapper;
import ru.rgrabelnikov.fbbackend.util.JwtProvider;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.rgrabelnikov.fbbackend.util.JwtProvider.CLAIM_ROLES;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    private final UserMapper userMapper;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if (jwtProvider.validateToken(token)) {
            String username = jwtProvider.extractLogin(token);

            List<String> roles = (List<String>) jwtProvider
                    .getClaimsFromToken(token)
                    .get(CLAIM_ROLES, List.class);
            Set<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(userMapper::toGrantedAuthority)
                    .collect(Collectors.toSet());

            return Mono.just(new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            ));
        }

        return Mono.empty();
    }
}