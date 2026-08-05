package mobile.enterprise.reporting;

import io.qameta.allure.Allure;
import mobile.enterprise.metrics.MobileExecutionStep;

public final class MobileAllurePublisher {

    private MobileAllurePublisher() {
    }

    public static void attach(MobileExecutionStep step) {
        String text = "Business objective: " + step.businessObjective() + System.lineSeparator()
                + "Workflow state: " + step.workflowState() + System.lineSeparator()
                + "Duration: " + step.durationMillis() + " ms" + System.lineSeparator()
                + "Result: " + (step.passed() ? "PASSED" : "FAILED");
        Allure.addAttachment("Mobile Step " + step.number() + " - " + step.name(), "text/plain", text);
    }
}
