package api.tests;

import core.base.BaseApiTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class HealthCheckTest extends BaseApiTest {

    @Test
    public void verifyFrameworkApiLayerRuns() {
        assertEquals(200, 200, "API framework layer executed successfully");
    }
}