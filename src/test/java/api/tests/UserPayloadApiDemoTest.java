package api.tests;

import api.clients.BaseApiClient;
import api.endpoints.ApiEndpoints;
import api.payloads.PayloadManager;
import api.validators.ResponseValidator;
import api.validators.SchemaValidator;
import core.base.BaseApiTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Epic("API Testing")
@Feature("API Demo")
public class UserPayloadApiDemoTest extends BaseApiTest {

    @Test
    @Story("POST a user payload and validate the echoed response")
    @Description("Demonstrates payload loading, request execution, field assertions, schema validation, and Allure evidence.")
    public void verifyUserPayloadPostFlow() {
        String payload = PayloadManager.getSampleUserPayload();
        Allure.addAttachment(
                "Request payload",
                "application/json",
                new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)),
                ".json"
        );

        Response response = BaseApiClient.request()
                .body(payload)
                .when()
                .post(ApiEndpoints.POST);

        Allure.addAttachment(
                "Response payload",
                "application/json",
                new ByteArrayInputStream(response.asPrettyString().getBytes(StandardCharsets.UTF_8)),
                ".json"
        );

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateContentTypeContains(response, "application/json");
        ResponseValidator.validateJsonPath(response, "json.firstName", "Aneesh");
        ResponseValidator.validateJsonPath(response, "json.lastName", "Neladri");
        ResponseValidator.validateJsonPath(response, "json.role", "QA");
        ResponseValidator.validateJsonPathNotEmpty(response, "origin");
        SchemaValidator.validateSchema(response, "schemas/api/httpbin-post-response-schema.json");
    }
}
