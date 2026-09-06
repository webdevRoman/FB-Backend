package ru.rgrabelnikov.fbbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import ru.rgrabelnikov.fbbackend.config.properties.SwaggerProperties;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MAX_INTEGER_VALUE;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MAX_LENGTH_255;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MAX_LENGTH_ARRAY;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MAX_LENGTH_DATE;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MAX_LENGTH_UUID;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MAX_NUMBER_VALUE;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MIN_INTEGER_VALUE;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MIN_NUMBER_VALUE;
import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.UUID_PATTERN;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.setInfo(swaggerProperties.info());
            openApi.servers(swaggerProperties.servers());

            final String securitySchemeName = "Authorization";
            openApi.addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
            openApi.getComponents().addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT"));

            openApi.getComponents().getSchemas().values().stream()
                    .filter(schema -> "object".equals(schema.getType()))
                    .forEach(schema -> schema.setAdditionalProperties(false));

            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                        if (isNull(operation.getDescription())) {
                            operation.setDescription(operation.getSummary());
                        }
                        final var parameters = operation.getParameters();
                        if (!isEmpty(parameters)) {
                            parameters.forEach(parameter -> setSchemaDefault(parameter.getSchema(), openApi));
                        }
                        final var request = operation.getRequestBody();
                        if (nonNull(request)) {
                            final var jsonRequest = request.getContent().get(MediaType.APPLICATION_JSON_VALUE);
                            if (nonNull(jsonRequest)) {
                                setSchemaDefault(jsonRequest.getSchema(), openApi);
                            }
                        }
                        final var responses = operation.getResponses();
                        if (!isEmpty(responses)) {
                            responses.values()
                                    .forEach(apiResponse -> {
                                                final var content = apiResponse.getContent();
                                                if (!isEmpty(content)) {
                                                    final var newContent = new Content();
                                                    content.forEach((key, value) -> {
                                                        setSchemaDefault(value.getSchema(), openApi);
                                                        if (key.equals("*/*")) {
                                                            newContent.addMediaType("application/json", value);
                                                        } else {
                                                            newContent.addMediaType(key, value);
                                                        }
                                                    });

                                                    apiResponse.setContent(newContent);
                                                }
                                            }
                                    );
                        }
                    })
            );
        };
    }

    private void setSchemaDefault(final Schema<?> schema, OpenAPI openApi) {
        if (schema instanceof ObjectSchema || schema instanceof ComposedSchema) {
            processObjectSchema(schema, openApi);
        } else if (schema instanceof ArraySchema arraySchema) {
            setArrayDefault(arraySchema, openApi);
        } else if (schema instanceof UUIDSchema) {
            setUuidDefault(schema);
        } else if (schema instanceof StringSchema) {
            setStringDefault(schema);
        } else if (schema instanceof DateSchema) {
            setDateDefault(schema);
        } else if (schema instanceof IntegerSchema && "int32".equalsIgnoreCase(schema.getFormat())) {
            setIntegerDefault(schema);
        } else if (schema instanceof IntegerSchema) {
            setNumberDefault(schema);
        } else if (schema instanceof NumberSchema) {
            setNumberDefault(schema);
        } else if (schema instanceof DateTimeSchema) {
            setDateTimeDefault(schema);
        } else if (nonNull(schema.get$ref())) {
            processRefSchema(schema, openApi);
        }
    }

    private void processObjectSchema(final Schema<?> schema, OpenAPI openApi) {
        if (schema instanceof ComposedSchema composedSchema && !isEmpty(composedSchema.getAllOf())) {
            composedSchema.getAllOf().forEach(it -> {
                if (isNull(it.get$ref())) {
                    copyElementProperties(schema, it, true);
                } else {
                    final var ref = it.get$ref().replace("#/components/schemas/", "");
                    final var refObj = openApi.getComponents().getSchemas().get(ref);
                    if (nonNull(refObj)) {
                        copyElementProperties(schema, refObj, false);
                    }
                }
            });
            composedSchema.setAllOf(null);
        }
        final Map<String, Schema> properties = schema.getProperties();
        if (!isEmpty(properties)) {
            properties.values().forEach(it -> setSchemaDefault(it, openApi));
        }
        final boolean additionalPropertiesValue = schema instanceof ComposedSchema composedSchema && !isEmpty(composedSchema.getOneOf());
        schema.setAdditionalProperties(additionalPropertiesValue);
    }

    @SuppressWarnings("unchecked")
    private void copyElementProperties(Schema<?> dest, Schema<?> src, Boolean isRewrite) {
        if (isNull(dest.getProperties())) {
            dest.setProperties(new LinkedHashMap<>());
        }
        final Map<String, Schema> properties = dest.getProperties();
        if (nonNull(src.getProperties())) {
            src.getProperties().forEach((propertyKey, propertyValue) -> {
                if (!properties.containsKey(propertyKey) || isRewrite) {
                    Schema schema = propertyValue;
                    if (nonNull(src.getDiscriminator()) && Objects.equals(src.getDiscriminator().getPropertyName(), propertyKey)) {
                        schema = new StringSchema()
                                .maxLength(propertyValue.getMaxLength())
                                .type("string")
                                .description(propertyValue.getDescription())
                                .maxProperties(propertyValue.getMaxProperties())
                                .nullable(propertyValue.getNullable())
                                .$ref(propertyValue.get$ref())
                                .name(propertyValue.getName())
                                .required(propertyValue.getRequired());
                    }
                    properties.put(propertyKey, schema);
                }
            });
        }
    }

    private void setArrayDefault(final ArraySchema schema, OpenAPI openApi) {
        if (isNull(schema.getMaxItems())) {
            schema.setMaxItems(MAX_LENGTH_ARRAY);
        }

        final Schema<?> items = schema.getItems();
        if (nonNull(items)) {
            setSchemaDefault(items, openApi);
        }
    }

    private void setUuidDefault(final Schema<?> schema) {
        schema.setFormat(null);
        schema.setMaxLength(MAX_LENGTH_UUID);
        schema.setPattern(UUID_PATTERN);
        schema.setExample("0feb3538-d1c1-4521-8f5a-0ae98dc20631");
    }

    private void setStringDefault(final Schema<?> schema) {
        if (isNull(schema.getMaxLength())) {
            schema.setMaxLength(MAX_LENGTH_255);
        }
    }

    private void setDateDefault(final Schema<?> schema) {
        schema.format(null)
                .maxLength(MAX_LENGTH_DATE)
                .pattern("^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$")
                .example("2023-03-29");
    }

    private void setIntegerDefault(final Schema<?> schema) {
        schema.setFormat(null);
        if (isNull(schema.getMinimum())) {
            schema.setMinimum(new BigDecimal(MIN_INTEGER_VALUE));
        }

        if (isNull(schema.getMaximum())) {
            schema.setMaximum(new BigDecimal(MAX_INTEGER_VALUE));
        }
    }

    private void setNumberDefault(final Schema<?> schema) {
        schema.setFormat(null);
        if (isNull(schema.getMinimum())) {
            schema.setMinimum(new BigDecimal(MIN_NUMBER_VALUE));
        }

        if (isNull(schema.getMaximum())) {
            schema.setMaximum(new BigDecimal(MAX_NUMBER_VALUE));
        }
    }

    private void setDateTimeDefault(final Schema<?> schema) {
        schema.format("date-time")
                .pattern("^[0-9]{4}-((0[1-9])|(1[0-2]))-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(()|(.[0-9]{1,6})(Z|([+-](0[0-9]|1[0-2]):([0-5][0-9])))|(.[0-9]{1,6}))$")
                .example("2023-12-14T13:04:50.169Z");
    }

    @SuppressWarnings("unchecked")
    private void processRefSchema(final Schema<?> schema, OpenAPI openApi) {
        final var ref = schema.get$ref().replace("#/components/schemas/", "");
        final var refObj = openApi.getComponents().getSchemas().get(ref);
        if (nonNull(refObj)) {
            copyElementProperties(schema, refObj, false);
            schema.setDiscriminator(refObj.getDiscriminator());
            schema.setOneOf(refObj.getOneOf());
        }
        final Map<String, Schema> properties = schema.getProperties();
        if (!isEmpty(properties)) {
            properties.values().forEach(it -> setSchemaDefault(it, openApi));
        }
        schema.setAdditionalProperties(false);
        if (nonNull(schema.getOneOf()) && !schema.getOneOf().isEmpty()) {
            schema.getOneOf().forEach(it -> setSchemaDefault(it, openApi));
        }
    }
}
