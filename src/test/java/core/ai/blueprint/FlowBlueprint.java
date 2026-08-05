package core.ai.blueprint;

import java.util.List;

public class FlowBlueprint {

    private final String name;
    private final List<String> steps;

    public FlowBlueprint(
            String name,
            List<String> steps
    ) {
        this.name = name;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public List<String> getSteps() {
        return steps;
    }
}
