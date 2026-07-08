package roomscan.workflow;

public class ScanWorkflow {

    private ScanState currentState =
            ScanState.IDLE;

    public ScanState getCurrentState() {
        return currentState;
    }

    public void moveTo(ScanState nextState) {

        if (!isValidTransition(currentState, nextState)) {
            throw new IllegalStateException(
                    "Invalid workflow transition from "
                            + currentState
                            + " to "
                            + nextState
            );
        }

        currentState = nextState;
    }

    private boolean isValidTransition(
            ScanState current,
            ScanState next
    ) {

        return switch (current) {

            case IDLE ->
                    next == ScanState.READY;

            case READY ->
                    next == ScanState.SCANNING
                            || next == ScanState.CANCELLED;

            case SCANNING ->
                    next == ScanState.PAUSED
                            || next == ScanState.COMPLETED
                            || next == ScanState.FAILED;

            case PAUSED ->
                    next == ScanState.SCANNING
                            || next == ScanState.CANCELLED;

            case COMPLETED ->
                    next == ScanState.REVIEW;

            case REVIEW ->
                    next == ScanState.UPLOADING
                            || next == ScanState.CANCELLED;

            case UPLOADING ->
                    next == ScanState.PROCESSED
                            || next == ScanState.FAILED;

            case FAILED ->
                    next == ScanState.READY
                            || next == ScanState.CANCELLED;

            case PROCESSED, CANCELLED ->
                    false;
        };
    }
}
