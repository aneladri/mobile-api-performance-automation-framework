package api.tests;

import api.auth.AuthManager;
import api.clients.BaseApiClient;
import api.endpoints.ApiEndpoints;
import api.validators.ResponseValidator;
import core.base.BaseApiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;

@Epic("API Testing")
@Feature("Authentication")
public class AuthenticationApiTest extends BaseApiTest {

    @Test
    @Story("Validate Basic authentication")
    public void verifyBasicAuthentication() {
        String username = "mapaf-user";
        String password = "mapaf-password";

        Response response = AuthManager.applyBasicAuth(
                        BaseApiClient.request(),
                        username,
                        password
                )
                .pathParam("username", username)
                .pathParam("password", password)
                .when()
                .get(ApiEndpoints.BASIC_AUTH);

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonPath(response, "authenticated", true);
        ResponseValidator.validateJsonPath(response, "user", username);
    }

    @Test
    @Story("Validate API key header transmission")
    public void verifyApiKeyHeaderIsSent() {
        String headerName = "X-API-Key";
        String apiKey = "demo-api-key";

        Response response = AuthManager.applyApiKey(
                        BaseApiClient.request(),
                        headerName,
                        apiKey
                )
                .when()
                .get(ApiEndpoints.HEADERS);

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonPath(response, "headers.X-Api-Key", apiKey);
    }
}
