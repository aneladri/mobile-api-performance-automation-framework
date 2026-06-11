package api.tests;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class HealthCheckTest {

    @Test
    public void verifyFrameworkRuns() {
        assertTrue(true, "Framework test executed successfully");
    }
}
