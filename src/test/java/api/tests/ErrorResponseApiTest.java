package api.tests;

import api.clients.BaseApiClient;
import api.endpoints.ApiEndpoints;
import api.validators.ResponseValidator;
import core.base.BaseApiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("API Testing")
@Feature("Expected Error Responses")
public class ErrorResponseApiTest extends BaseApiTest {

    @DataProvider(name = "expectedErrorStatuses")
    public Object[][] expectedErrorStatuses() {
        return new Object[][]{
                {400},
                {404},
                {500}
        };
    }

    @Test(dataProvider = "expectedErrorStatuses")
    @Story("Validate expected 4xx and 5xx responses")
    public void verifyExpectedErrorResponse(int expectedStatusCode) {
        Response response = BaseApiClient.request()
                .pathParam("code", expectedStatusCode)
                .when()
                .get(ApiEndpoints.STATUS_CODE);

        ResponseValidator.validateExpectedErrorStatus(response, expectedStatusCode);
    }
}
