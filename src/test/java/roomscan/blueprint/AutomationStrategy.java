package roomscan.blueprint;

import java.util.List;

public class AutomationStrategy {

    private final CaptureStrategy captureStrategy;
    private final List<ValidationStrategy> validationStrategies;
    private final List<String> fixtures;
    private final List<String> risks;

    public AutomationStrategy(
            CaptureStrategy captureStrategy,
            List<ValidationStrategy> validationStrategies,
            List<String> fixtures,
            List<String> risks
    ) {
        this.captureStrategy = captureStrategy;
        this.validationStrategies = validationStrategies;
        this.fixtures = fixtures;
        this.risks = risks;
    }

    public CaptureStrategy getCaptureStrategy() {
        return captureStrategy;
    }

    public List<ValidationStrategy> getValidationStrategies() {
        return validationStrategies;
    }

    public List<String> getFixtures() {
        return fixtures;
    }

    public List<String> getRisks() {
        return risks;
    }
}
