package ru.rgrabelnikov.fbbackend.config.properties;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("swagger")
public record SwaggerProperties(

        @Valid
        @NotNull
        List<Server> servers,

        @Valid
        @NotNull
        Info info
) {
}
