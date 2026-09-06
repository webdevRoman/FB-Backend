package ru.rgrabelnikov.fbbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ru.rgrabelnikov.fbbackend.config.properties.JwtProperties;
import ru.rgrabelnikov.fbbackend.domain.UserEntity;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Log4j2
@Component
public class JwtProvider {

    private final static String CLAIM_UID = "uid";
    private final static String CLAIM_ROLES = "roles";

    private final JwtProperties jwtProperties;

    private final SecretKey accessTokenKey;

    public JwtProvider(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.accessTokenKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.secret().getBytes()));
    }

    public String generateToken(final UserEntity userEntity) {
        log.info("Generating token for user {}", userEntity.getLogin());
        final Date creationDate = new Date();
        final Date expirationDate = new Date(creationDate.getTime() + jwtProperties.expiration().toMillis());

        return Jwts.builder()
                .subject(userEntity.getLogin())
                .issuedAt(creationDate)
                .expiration(expirationDate)
                .claim(CLAIM_UID, userEntity.getId())
                .claim(CLAIM_ROLES, List.of(userEntity.getRole()))
                .signWith(accessTokenKey)
                .compact();
    }

    public Claims getClaimsFromToken(final String token) {
        return Jwts.parser()
                .verifyWith(accessTokenKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractLogin(final String token) {
        return getClaimsFromToken(token)
                .getSubject();
    }

    public UUID extractId(final String token) {
        return UUID.fromString(getClaimsFromToken(token)
                .get(CLAIM_UID, String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(final String token) {
        return getClaimsFromToken(token)
                .get(CLAIM_ROLES, List.class)
                .stream()
                .map(Object::toString)
                .toList();
    }

    public boolean validateToken(final String token) {
        try {
            return getClaimsFromToken(token)
                    .getExpiration()
                    .after(new Date());
        } catch (final ExpiredJwtException ex) {
            log.error("Token expired");
        } catch (final MalformedJwtException ex) {
            log.error("Token malformed");
        } catch (final SignatureException ex) {
            log.error("Token signature error");
        } catch (final Exception ex) {
            log.error("Unexpected token parsing error", ex);
        }
        return false;
    }
}
