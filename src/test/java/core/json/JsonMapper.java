package core.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonMapper {

    private static final ObjectMapper OBJECT_MAPPER =
            createMapper();

    private JsonMapper() {
    }

    private static ObjectMapper createMapper() {

        ObjectMapper mapper =
                new ObjectMapper();

        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
        );

        return mapper;
    }

    public static ObjectMapper getInstance() {
        return OBJECT_MAPPER;
    }
}
