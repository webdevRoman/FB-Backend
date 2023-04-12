package ru.rgrabelnikov.fbbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ru.rgrabelnikov.fbbackend.config.JwtConfig;
import ru.rgrabelnikov.fbbackend.model.UserEntity;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;


@Component
@Log4j2
public class JwtProvider {

    public final static String CLAIM_UID = "uid";
    public final static String CLAIM_ROLES = "roles";

    private final JwtConfig jwtConfig;

    private final SecretKey accessTokenKey;

    public JwtProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.accessTokenKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtConfig.getSecret().getBytes()));
    }

    public String generateToken(UserEntity userEntity) {
        log.info("Генерация токена пользователя {}", userEntity.getLogin());
        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + jwtConfig.getExpiration().toMillis());

        return Jwts.builder()
                .setSubject(userEntity.getLogin())
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .claim(CLAIM_UID, userEntity.getId())
                .claim(CLAIM_ROLES, List.of(userEntity.getRole()))
                .signWith(accessTokenKey)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessTokenKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractLogin(String token) {
        return getClaimsFromToken(token)
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            return getClaimsFromToken(token)
                    .getExpiration()
                    .after(new Date());
        } catch (ExpiredJwtException ex) {
            log.error("Токен истёк");
        } catch (MalformedJwtException ex) {
            log.error("Деформированный токен");
        } catch (SignatureException ex) {
            log.error("Ошибка подписи токена");
        } catch (Exception ex) {
            log.error("Непредвиденная ошибка парсинга токена", ex);
        }
        return false;
    }
}