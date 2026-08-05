package ai.tests;

import core.ai.generation.AutomationGenerationEngine;
import core.ai.generation.AutomationProject;
import core.ai.models.AutomationGenerationRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class AutomationGenerationEngineTest {

    @Test
    public void verifyAutomationGenerationEngineCanBeCreated() {

        AutomationGenerationEngine engine =
                new AutomationGenerationEngine();

        Assert.assertNotNull(engine);
    }

    @Test
    public void verifyAutomationGenerationRequestCanBePrepared() {

        AutomationGenerationRequest request =
                new AutomationGenerationRequest(
                        "As a user, I can login successfully",
                        "android",
                        "LoginScreen",
                        "User enters valid credentials and lands on Home screen",
                        "mobile"
                );

        Assert.assertEquals(
                request.getScreenName(),
                "LoginScreen"
        );

        Assert.assertEquals(
                request.getPlatform(),
                "android"
        );
    }
}
