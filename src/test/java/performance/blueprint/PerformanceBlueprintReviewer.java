package performance.blueprint;

import java.util.ArrayList;
import java.util.List;

public class PerformanceBlueprintReviewer {

    public List<String> review(
            PerformanceBlueprint blueprint
    ) {
        List<String> findings =
                new ArrayList<>();

        if (blueprint.getName() == null
                || blueprint.getName().isBlank()) {
            findings.add("Performance blueprint name is missing");
        }

        if (blueprint.getSource() == null
                || blueprint.getSource().isBlank()) {
            findings.add("Performance blueprint source is missing");
        }

        if (blueprint.getScenarios() == null
                || blueprint.getScenarios().isEmpty()) {
            findings.add("No performance scenarios selected");
        }

        if (blueprint.getProfiles() == null
                || blueprint.getProfiles().isEmpty()) {
            findings.add("No performance profiles selected");
        }

        if (blueprint.getSlas() == null
                || blueprint.getSlas().isEmpty()) {
            findings.add("No performance SLAs defined");
        }

        return findings;
    }

    public boolean isApproved(
            PerformanceBlueprint blueprint
    ) {
        return review(blueprint).isEmpty();
    }
}
