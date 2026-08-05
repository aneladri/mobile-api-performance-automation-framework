package api.clients;

import api.builders.RequestBuilder;
import core.config.ConfigManager;
import io.restassured.specification.RequestSpecification;

public class BaseApiClient {

    private BaseApiClient() {
    }

    public static RequestSpecification request() {

        return RequestBuilder.defaultRequest()
                .baseUri(
                        ConfigManager.getRequired("baseUrl")
                );
    }
}