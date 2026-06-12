package core.listeners;

import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.*;

public class TestListener implements ITestListener {

    private static final Logger logger =
            LoggerUtil.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {

        logger.info(
                "STARTED : {}",
                result.getMethod().getMethodName()
        );
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        logger.info(
                "PASSED : {}",
                result.getMethod().getMethodName()
        );
    }

    @Override
    public void onTestFailure(ITestResult result) {

        logger.error(
                "FAILED : {}",
                result.getMethod().getMethodName()
        );

        logger.error(
                result.getThrowable()
        );
    }

    @Override
    public void onTestSkipped(ITestResult result) {

        logger.warn(
                "SKIPPED : {}",
                result.getMethod().getMethodName()
        );
    }
}