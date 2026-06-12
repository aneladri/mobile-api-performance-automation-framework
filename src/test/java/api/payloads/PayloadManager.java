package api.payloads;

import core.utils.JsonUtils;

public final class PayloadManager {

    private PayloadManager() {
    }

    public static String getSampleUserPayload() {

        return JsonUtils.getJsonAsString(
                "src/test/resources/testdata/api/sample-user.json"
        );
    }
}
