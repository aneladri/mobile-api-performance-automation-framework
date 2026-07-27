package core.ai.analysis.tests;

import core.ai.analysis.FailureConfidence;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FailureConfidenceTest {

    @Test
    public void shouldParseConfidenceIgnoringCase() {

        Assert.assertEquals(
                FailureConfidence.from("high"),
                FailureConfidence.HIGH
        );

        Assert.assertEquals(
                FailureConfidence.from(" Medium "),
                FailureConfidence.MEDIUM
        );
    }

    @Test
    public void shouldReturnUnknownForInvalidValue() {

        Assert.assertEquals(
                FailureConfidence.from("certain"),
                FailureConfidence.UNKNOWN
        );

        Assert.assertEquals(
                FailureConfidence.from(null),
                FailureConfidence.UNKNOWN
        );
    }
}
