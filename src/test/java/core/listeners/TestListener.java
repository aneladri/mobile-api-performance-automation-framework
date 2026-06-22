package core.listeners;

import core.ai.FailureAnalysisAgent;
import core.ai.ReportWriter;
import core.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

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
        String testName =
                result.getMethod().getMethodName();

        logger.error(
                "FAILED : {}",
                testName
        );

        Throwable throwable =
                result.getThrowable();

        if (throwable != null) {
            logger.error(
                    throwable
            );

            runFailureAnalysis(
                    testName,
                    throwable
            );
        }
    }

    private void runFailureAnalysis(
            String testName,
            Throwable throwable
    ) {
        try {
            FailureAnalysisAgent agent =
                    new FailureAnalysisAgent();

            String analysis =
                    agent.analyze(
                            throwable.toString()
                    );

            logger.info(
                    "AI Failure Analysis for {}:\n{}",
                    testName,
                    analysis
            );

            ReportWriter.writeReport(
                    analysis,
                    "failure-" + testName + ".md"
            );

        } catch (Exception e) {
            logger.warn(
                    "AI failure analysis skipped for {}: {}",
                    testName,
                    e.getMessage()
            );
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn(
                "SKIPPED : {}",
                result.getMethod().getMethodName()
        );
    }
}