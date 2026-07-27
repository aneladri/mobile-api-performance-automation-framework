package common.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private final int maxRetries;
    private int retryCount = 0;

    public RetryAnalyzer() {
        this.maxRetries = Integer.parseInt(
                System.getProperty("mapaf.retry.count", "0")
        );
    }

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount >= maxRetries) {
            return false;
        }

        retryCount++;

        System.out.printf(
                "[MAPAF Retry] %s - Retry %d of %d%n",
                result.getMethod().getMethodName(),
                retryCount,
                maxRetries
        );

        return true;
    }
}
