package ru.rgrabelnikov.fbbackend.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "fb.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @NotNull Duration expiration
) {

    public JwtProperties(
            @NotBlank
            @DefaultValue("u6FWEJ8T1Ein65XffiaxyAc1dikva3HJSdVIDXvUCxaubnionZVHwal2SmozDCL5HJvep9Ypjik619zwS9GkJKGJwKWOT2QuEUVB")
            String secret,

            @NotNull
            @DefaultValue("PT8H")
            Duration expiration
    ) {
        this.secret = secret;
        this.expiration = expiration;
    }
}
