package api.clients;

import core.config.ConfigManager;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseApiClient {

    private BaseApiClient() {
    }

    public static RequestSpecification request() {
        return given()
                .baseUri(ConfigManager.getRequired("baseUrl"))
                .header("Content-Type", "application/json")
                .log().all();
    }
}