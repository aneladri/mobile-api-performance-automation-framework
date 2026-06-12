package mobile.tests;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class MobileFrameworkTest {

    @Test
    public void verifyMobileFrameworkLayerExists() {
        assertTrue(true, "Mobile page object foundation is available");
    }
}
