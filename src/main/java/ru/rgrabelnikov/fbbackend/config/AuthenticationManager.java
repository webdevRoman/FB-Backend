package ru.rgrabelnikov.fbbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.security.ContextUser;
import ru.rgrabelnikov.fbbackend.mapper.UserMapper;
import ru.rgrabelnikov.fbbackend.util.JwtProvider;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    private final UserMapper userMapper;

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        if (isNull(authentication.getCredentials())) {
            return Mono.empty();
        }
        final String token = authentication.getCredentials().toString();

        if (jwtProvider.validateToken(token)) {
            final UUID id = jwtProvider.extractId(token);
            final String username = jwtProvider.extractLogin(token);
            final List<String> roles = jwtProvider.extractRoles(token);
            final List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(userMapper::toGrantedAuthority)
                    .toList();

            final ContextUser contextUser = userMapper.toContextUser(id, username, authorities);
            return Mono.just(new UsernamePasswordAuthenticationToken(
                    contextUser,
                    null,
                    authorities
            ));
        }

        return Mono.empty();
    }
}
