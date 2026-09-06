package ru.rgrabelnikov.fbbackend.testutil;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.StreamUtils.copyToString;

@NoArgsConstructor(access = PRIVATE)
public final class TestUtils {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .changeDefaultPropertyInclusion(incl -> {
                incl.withValueInclusion(JsonInclude.Include.NON_NULL);
                incl.withContentInclusion(JsonInclude.Include.NON_NULL);
                return incl;
            })
            .build();

    public static <T> T getAsObject(final String path, final TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(new ClassPathResource(path).getInputStream(), typeReference);
        } catch (Exception ex) {
            throw new IllegalStateException("Error on reading resource", ex);
        }
    }

    public static <T> T getAsObject(final String path, final Class<T> classType) {
        try {
            return OBJECT_MAPPER.readValue(new ClassPathResource(path).getInputStream(), classType);
        } catch (Exception ex) {
            throw new IllegalStateException("Error on reading resource", ex);
        }
    }

    public static String getAsString(final String path) {
        try {
            return copyToString(new DefaultResourceLoader().getResource(path).getInputStream(), UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Error on reading resource", ex);
        }
    }
}
