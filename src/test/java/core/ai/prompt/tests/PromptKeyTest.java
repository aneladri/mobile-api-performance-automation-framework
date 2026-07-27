package core.ai.prompt.tests;

import core.ai.prompt.PromptKey;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PromptKeyTest {

    @Test
    public void shouldCreatePromptKey() {

        PromptKey key =
                PromptKey.of(
                        "failure-analysis",
                        "v1"
                );

        Assert.assertEquals(
                key.getName(),
                "failure-analysis"
        );

        Assert.assertEquals(
                key.getVersion(),
                "v1"
        );

        Assert.assertEquals(
                key.toString(),
                "failure-analysis:v1"
        );
    }

    @Test
    public void shouldCompareEqualKeys() {

        PromptKey first =
                PromptKey.of(
                        "failure-analysis",
                        "v1"
                );

        PromptKey second =
                PromptKey.of(
                        "failure-analysis",
                        "v1"
                );

        Assert.assertEquals(first, second);
        Assert.assertEquals(
                first.hashCode(),
                second.hashCode()
        );
    }

    @Test
    public void shouldTrimValues() {

        PromptKey key =
                PromptKey.of(
                        " failure-analysis ",
                        " v1 "
                );

        Assert.assertEquals(
                key.getName(),
                "failure-analysis"
        );

        Assert.assertEquals(
                key.getVersion(),
                "v1"
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class
    )
    public void shouldRejectBlankName() {

        PromptKey.of(
                " ",
                "v1"
        );
    }

    @Test(
            expectedExceptions =
                    IllegalArgumentException.class
    )
    public void shouldRejectBlankVersion() {

        PromptKey.of(
                "failure-analysis",
                " "
        );
    }
}
