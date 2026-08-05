package core.ai.prompt.tests;

import core.ai.prompt.PromptVariables;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class PromptVariablesTest {

    @Test
    public void shouldStoreVariables() {

        PromptVariables variables =
                PromptVariables.builder()
                        .put("testName", "Login Test")
                        .put("locator", "#username")
                        .build();

        Assert.assertEquals(
                variables.get("testName"),
                "Login Test"
        );

        Assert.assertEquals(
                variables.get("locator"),
                "#username"
        );

        Assert.assertEquals(
                variables.size(),
                2
        );
    }

    @Test
    public void shouldConvertNullValueToEmptyString() {

        PromptVariables variables =
                PromptVariables.builder()
                        .put("error", null)
                        .build();

        Assert.assertEquals(
                variables.get("error"),
                ""
        );
    }

    @Test(
            expectedExceptions = NullPointerException.class
    )
    public void shouldRejectNullKey() {

        PromptVariables.builder()
                .put(null, "value")
                .build();
    }

    @Test(
            expectedExceptions = UnsupportedOperationException.class
    )
    public void shouldReturnImmutableMap() {

        PromptVariables variables =
                PromptVariables.builder()
                        .put("a", "1")
                        .build();

        Map<String, String> map =
                variables.asMap();

        map.put("b", "2");
    }

    @Test
    public void shouldOverwriteDuplicateKeys() {

        PromptVariables variables =
                PromptVariables.builder()
                        .put("name", "old")
                        .put("name", "new")
                        .build();

        Assert.assertEquals(
                variables.get("name"),
                "new"
        );

        Assert.assertEquals(
                variables.size(),
                1
        );
    }
}
