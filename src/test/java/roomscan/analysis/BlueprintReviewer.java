package roomscan.analysis;

import roomscan.blueprint.RoomScanBlueprint;

import java.util.ArrayList;
import java.util.List;

public class BlueprintReviewer {

    public List<String> review(
            RoomScanBlueprint blueprint
    ) {
        List<String> findings =
                new ArrayList<>();

        if (blueprint.getFeatureName() == null
                || blueprint.getFeatureName().isBlank()) {
            findings.add("Feature name is missing");
        }

        if (blueprint.getWorkflowStates().isEmpty()) {
            findings.add("Workflow states are missing");
        }

        if (blueprint.getAutomationStrategy()
                .getFixtures()
                .isEmpty()) {
            findings.add("No scan fixtures selected");
        }

        if (blueprint.getUiValidations().isEmpty()) {
            findings.add("UI validations are missing");
        }

        if (blueprint.getApiValidations().isEmpty()) {
            findings.add("API validations are missing");
        }

        return findings;
    }

    public boolean isApproved(
            RoomScanBlueprint blueprint
    ) {
        return review(blueprint).isEmpty();
    }
}
