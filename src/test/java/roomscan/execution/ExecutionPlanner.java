package roomscan.execution;

import roomscan.blueprint.RoomScanBlueprint;
import roomscan.workflow.ScanState;

import java.util.ArrayList;
import java.util.List;

public class ExecutionPlanner {

    public ExecutionPlan createPlan(
            RoomScanBlueprint blueprint
    ) {
        List<ExecutionStep> steps =
                new ArrayList<>();

        for (ScanState state : blueprint.getWorkflowStates()) {
            steps.add(
                    toStep(state)
            );
        }

        return new ExecutionPlan(
                blueprint.getFeatureName(),
                steps
        );
    }

    private ExecutionStep toStep(
            ScanState state
    ) {
        return switch (state) {

            case IDLE ->
                    new ExecutionStep(
                            "Verify Initial State",
                            "Verify room scan flow starts in idle state"
                    );

            case READY ->
                    new ExecutionStep(
                            "Prepare Scan",
                            "Prepare the room scan workflow"
                    );

            case SCANNING ->
                    new ExecutionStep(
                            "Start Scan",
                            "Start room capture using configured capture provider"
                    );

            case PAUSED ->
                    new ExecutionStep(
                            "Pause Scan",
                            "Pause the active room scan"
                    );

            case COMPLETED ->
                    new ExecutionStep(
                            "Finish Scan",
                            "Complete capture and generate scan result"
                    );

            case REVIEW ->
                    new ExecutionStep(
                            "Review Scan",
                            "Review scan summary and validation details"
                    );

            case UPLOADING ->
                    new ExecutionStep(
                            "Upload Scan",
                            "Upload scan result for backend processing"
                    );

            case PROCESSED ->
                    new ExecutionStep(
                            "Verify Processed Scan",
                            "Verify scan processing has completed"
                    );

            case FAILED ->
                    new ExecutionStep(
                            "Validate Failure Handling",
                            "Verify failure state and recovery messaging"
                    );

            case CANCELLED ->
                    new ExecutionStep(
                            "Cancel Scan",
                            "Cancel scan and return to starting state"
                    );
        };
    }
}
