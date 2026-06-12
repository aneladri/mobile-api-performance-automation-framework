package api.validators;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public final class SchemaValidator {

    private SchemaValidator() {
    }

    public static void validateSchema(Response response, String schemaPath) {
        response.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
    }
}
