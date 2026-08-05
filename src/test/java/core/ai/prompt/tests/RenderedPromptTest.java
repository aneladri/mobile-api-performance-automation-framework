package core.ai.prompt.tests;

import core.ai.prompt.RenderedPrompt;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RenderedPromptTest {

    @Test
    public void shouldCreateRenderedPrompt() {

        RenderedPrompt prompt =
                RenderedPrompt.builder()
                        .promptName("failure-analysis")
                        .promptVersion("v1")
                        .systemMessage("system")
                        .userMessage("user")
                        .build();

        Assert.assertEquals(
                prompt.getPromptName(),
                "failure-analysis"
        );

        Assert.assertEquals(
                prompt.getPromptVersion(),
                "v1"
        );

        Assert.assertEquals(
                prompt.getSystemMessage(),
                "system"
        );

        Assert.assertEquals(
                prompt.getUserMessage(),
                "user"
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class
    )
    public void shouldRejectBlankPromptName() {

        RenderedPrompt.builder()
                .promptName(" ")
                .promptVersion("v1")
                .systemMessage("system")
                .userMessage("user")
                .build();
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class
    )
    public void shouldRejectBlankPromptVersion() {

        RenderedPrompt.builder()
                .promptName("failure-analysis")
                .promptVersion("")
                .systemMessage("system")
                .userMessage("user")
                .build();
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class
    )
    public void shouldRejectBlankSystemMessage() {

        RenderedPrompt.builder()
                .promptName("failure-analysis")
                .promptVersion("v1")
                .systemMessage(" ")
                .userMessage("user")
                .build();
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class
    )
    public void shouldRejectBlankUserMessage() {

        RenderedPrompt.builder()
                .promptName("failure-analysis")
                .promptVersion("v1")
                .systemMessage("system")
                .userMessage("")
                .build();
    }
}
