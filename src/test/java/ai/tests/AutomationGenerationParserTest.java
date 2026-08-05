package ai.tests;

import core.ai.models.AutomationGenerationParser;
import core.ai.models.AutomationGenerationResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AutomationGenerationParserTest {

    @Test
    public void verifyGeneratedAutomationContentCanBeParsed() {

        String content =
                """
                SCREEN_OBJECT
                public class LoginScreen {}

                BUSINESS_FLOW
                public class LoginFlow {}

                TEST_CLASS
                public class LoginTest {}

                ASSERTIONS
                User should be logged in

                TEST_DATA
                TEST_USERNAME
                TEST_PASSWORD

                TODO_ITEMS
                Add real locators
                """;

        AutomationGenerationResponse response =
                AutomationGenerationParser.parse(content);

        Assert.assertTrue(
                response.getScreenObject().contains("LoginScreen")
        );

        Assert.assertTrue(
                response.getBusinessFlow().contains("LoginFlow")
        );

        Assert.assertTrue(
                response.getTestClass().contains("LoginTest")
        );

        Assert.assertTrue(
                response.getAssertions().contains("logged in")
        );

        Assert.assertTrue(
                response.getTestData().contains("TEST_USERNAME")
        );

        Assert.assertTrue(
                response.getTodoItems().contains("real locators")
        );
    }


    @Test
    public void shouldParseOnlyExactSectionMarkerLines() {
        String content = """
                SCREEN_OBJECT
                ```java
                package generated.test;
                public class SampleScreen {
                    private static final String LABEL = "BUSINESS_FLOW";
                }
                ```
                BUSINESS_FLOW
                package generated.test;
                public class SampleFlow {}
                TEST_CLASS
                package generated.test;
                public class SampleTest {}
                ASSERTIONS
                Screen is displayed
                TEST_DATA
                NONE
                TODO_ITEMS
                NONE
                """;

        AutomationGenerationResponse response =
                AutomationGenerationParser.parse(content);

        Assert.assertTrue(response.getScreenObject().contains("BUSINESS_FLOW"));
        Assert.assertFalse(response.getScreenObject().contains("```"));
        Assert.assertTrue(response.getBusinessFlow().contains("SampleFlow"));
    }
}
