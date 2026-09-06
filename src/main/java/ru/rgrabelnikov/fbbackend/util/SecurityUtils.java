package ru.rgrabelnikov.fbbackend.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.security.ContextUser;
import ru.rgrabelnikov.fbbackend.exception.UnauthorizedException;

@UtilityClass
public final class SecurityUtils {

    public static Mono<ContextUser> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .mapNotNull(Authentication::getPrincipal)
                .filter(it -> it instanceof ContextUser)
                .cast(ContextUser.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UnauthorizedException("Could not get user from security context"))));
    }
}
