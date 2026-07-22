package roomscan.execution;

import java.util.List;

public class ExecutionPlan {

    private final String featureName;
    private final List<ExecutionStep> steps;

    public ExecutionPlan(
            String featureName,
            List<ExecutionStep> steps
    ) {
        this.featureName = featureName;
        this.steps = steps;
    }

    public String getFeatureName() {
        return featureName;
    }

    public List<ExecutionStep> getSteps() {
        return steps;
    }
}
