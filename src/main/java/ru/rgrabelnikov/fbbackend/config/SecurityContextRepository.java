package ru.rgrabelnikov.fbbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(final ServerWebExchange exchange, final SecurityContext context) {
        throw new UnsupportedOperationException("Save method not supported");
    }

    @Override
    public Mono<SecurityContext> load(final ServerWebExchange exchange) {
        final String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            return authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(token, token))
                    .map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }
}
