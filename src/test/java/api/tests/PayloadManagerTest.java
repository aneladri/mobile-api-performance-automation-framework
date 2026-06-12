package api.tests;

import api.payloads.PayloadManager;
import core.base.BaseApiTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class PayloadManagerTest extends BaseApiTest {

    @Test
    public void verifyPayloadCanBeLoaded() {

        String payload =
                PayloadManager.getSampleUserPayload();

        assertTrue(
                payload.contains("Aneesh")
        );
    }
}
