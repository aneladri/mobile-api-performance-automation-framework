package roomscan.workflow;

import org.testng.Assert;

public final class ScanWorkflowValidator {

    private ScanWorkflowValidator() {
    }

    public static void assertState(
            ScanWorkflow workflow,
            ScanState expected
    ) {

        Assert.assertEquals(
                workflow.getCurrentState(),
                expected,
                "Unexpected workflow state"
        );
    }

    public static void assertCompleted(
            ScanWorkflow workflow
    ) {

        assertState(
                workflow,
                ScanState.COMPLETED
        );
    }

    public static void assertUploading(
            ScanWorkflow workflow
    ) {

        assertState(
                workflow,
                ScanState.UPLOADING
        );
    }

    public static void assertProcessed(
            ScanWorkflow workflow
    ) {

        assertState(
                workflow,
                ScanState.PROCESSED
        );
    }

    public static void assertFailed(
            ScanWorkflow workflow
    ) {

        assertState(
                workflow,
                ScanState.FAILED
        );
    }
}
