package core.ai.blueprint;

import java.util.List;

public class TestBlueprint {

    private final String name;
    private final List<String> assertions;

    public TestBlueprint(
            String name,
            List<String> assertions
    ) {
        this.name = name;
        this.assertions = assertions;
    }

    public String getName() {
        return name;
    }

    public List<String> getAssertions() {
        return assertions;
    }
}
