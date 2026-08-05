package core.enterprise.tests;

import core.enterprise.reporting.BusinessTransaction;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BusinessTransactionTest {

    @Test
    public void shouldCreatePassedTransactionFromExecutionStep() {
        BusinessTransaction transaction =
                BusinessTransaction.fromStep("Create Scan Session", 1250, true);

        Assert.assertEquals(transaction.name(), "Create Scan Session");
        Assert.assertEquals(transaction.requests(), 1);
        Assert.assertEquals(transaction.passed(), 1);
        Assert.assertEquals(transaction.failed(), 0);
        Assert.assertEquals(transaction.errorRatePercent(), 0.0);
        Assert.assertEquals(transaction.p95Ms(), 1250.0);
        Assert.assertEquals(transaction.status(), "PASS");
    }

    @Test
    public void shouldCreateFailedTransactionFromExecutionStep() {
        BusinessTransaction transaction =
                BusinessTransaction.fromStep("Upload Room Images", 870, false);

        Assert.assertEquals(transaction.requests(), 1);
        Assert.assertEquals(transaction.passed(), 0);
        Assert.assertEquals(transaction.failed(), 1);
        Assert.assertEquals(transaction.errorRatePercent(), 100.0);
        Assert.assertEquals(transaction.status(), "FAIL");
    }
}
