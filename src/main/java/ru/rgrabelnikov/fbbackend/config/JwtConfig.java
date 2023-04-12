package ru.rgrabelnikov.fbbackend.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "fb.jwt")
public class JwtConfig {

    @Valid
    @NotBlank
    private String secret =
            "u6FWEJ8T1Ein65XffiaxyAc1dikva3HJSdVIDXvUCxaubnionZVHwal2SmozDCL5HJvep9Ypjik619zwS9GkJKGJwKWOT2QuEUVB";

    @Valid
    @NotNull
    private Duration expiration = Duration.ofHours(8L);
}