package roomscan.analysis;

import roomscan.blueprint.AutomationStrategy;
import roomscan.blueprint.CaptureStrategy;
import roomscan.blueprint.RoomScanBlueprint;
import roomscan.blueprint.ValidationStrategy;
import roomscan.fixtures.ScanFixtureManager;
import roomscan.workflow.ScanState;

import java.util.ArrayList;
import java.util.List;

public class BlueprintGenerator {

    private final RoomRequirementAnalyzer analyzer =
            new RoomRequirementAnalyzer();

    public RoomScanBlueprint generate(
            String featureName,
            String requirement
    ) {
        List<String> capabilities =
                analyzer.identifyCapabilities(requirement);

        List<ScanState> states =
                buildWorkflowStates(capabilities);

        AutomationStrategy strategy =
                buildAutomationStrategy(capabilities);

        return new RoomScanBlueprint(
                featureName,
                requirement,
                states,
                strategy,
                buildUiValidations(capabilities),
                buildApiValidations(capabilities),
                buildPerformanceValidations(capabilities)
        );
    }

    private List<ScanState> buildWorkflowStates(
            List<String> capabilities
    ) {
        List<ScanState> states =
                new ArrayList<>();

        states.add(ScanState.IDLE);
        states.add(ScanState.READY);

        if (capabilities.contains("ROOM_SCAN")) {
            states.add(ScanState.SCANNING);
            states.add(ScanState.COMPLETED);
        }

        if (capabilities.contains("SCAN_REVIEW")) {
            states.add(ScanState.REVIEW);
        }

        if (capabilities.contains("SCAN_UPLOAD")) {
            states.add(ScanState.UPLOADING);
            states.add(ScanState.PROCESSED);
        }

        return states;
    }

    private AutomationStrategy buildAutomationStrategy(
            List<String> capabilities
    ) {
        List<String> fixtures =
                new ArrayList<>();

        fixtures.add(
                ScanFixtureManager.SMALL_BEDROOM_CLEAN
        );

        if (capabilities.contains("LOW_LIGHT_VALIDATION")) {
            fixtures.add(
                    ScanFixtureManager.LOW_LIGHT_WARNING_SCAN
            );
        }

        return new AutomationStrategy(
                CaptureStrategy.HYBRID,
                List.of(
                        ValidationStrategy.UI_AND_API,
                        ValidationStrategy.PERFORMANCE,
                        ValidationStrategy.REAL_DEVICE
                ),
                fixtures,
                List.of(
                        "Camera and LiDAR validation require real-device coverage",
                        "Mock capture validates workflow but not spatial accuracy"
                )
        );
    }

    private List<String> buildUiValidations(
            List<String> capabilities
    ) {
        List<String> validations =
                new ArrayList<>();

        validations.add("Room scan entry point is visible");
        validations.add("Scan can be started");

        if (capabilities.contains("SCAN_REVIEW")) {
            validations.add("Scan summary is displayed");
        }

        if (capabilities.contains("SCAN_UPLOAD")) {
            validations.add("Upload success message is displayed");
        }

        return validations;
    }

    private List<String> buildApiValidations(
            List<String> capabilities
    ) {
        List<String> validations =
                new ArrayList<>();

        if (capabilities.contains("SCAN_UPLOAD")) {
            validations.add("Upload API returns scan id");
            validations.add("Processing status reaches completed");
        }

        return validations;
    }

    private List<String> buildPerformanceValidations(
            List<String> capabilities
    ) {
        List<String> validations =
                new ArrayList<>();

        if (capabilities.contains("SCAN_UPLOAD")) {
            validations.add("Upload completes within SLA");
            validations.add("Processing latency remains within baseline");
        }

        return validations;
    }
}
