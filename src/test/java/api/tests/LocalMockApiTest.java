package api.tests;

import api.clients.BaseApiClient;
import api.endpoints.ApiEndpoints;
import api.mock.ApiMockServer;
import api.validators.ResponseValidator;
import core.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class LocalMockApiTest extends BaseApiTest {

    @Test
    public void verifyEmbeddedMockServerIsRunning() {
        assertTrue(ApiMockServer.isRunning(), "Embedded API mock server should be running");

        Response response = BaseApiClient.request()
                .when()
                .get(ApiEndpoints.STATUS_200);

        ResponseValidator.validateStatusCode(response, 200);
    }
}
