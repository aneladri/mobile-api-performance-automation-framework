package core.ai.blueprint;

import java.util.List;

public class ScreenBlueprint {

    private final String name;
    private final List<String> actions;
    private final List<String> validations;

    public ScreenBlueprint(
            String name,
            List<String> actions,
            List<String> validations
    ) {
        this.name = name;
        this.actions = actions;
        this.validations = validations;
    }

    public String getName() {
        return name;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<String> getValidations() {
        return validations;
    }
}
