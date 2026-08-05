package enterprise.story;

import java.util.List;

public final class RoomScanStory {

    public static final String DOMAIN = "RoomScan";
    public static final String OBJECTIVE =
            "Validate the complete RoomScan workflow from room-image capture through AI-generated floor-plan approval and release readiness.";

    private RoomScanStory() {
    }

    public static BusinessScenario endToEndScenario() {
        List<BusinessTransaction> transactions = List.of(
                transaction("RS-01", "Authenticate Field Technician", WorkflowStage.AUTHENTICATE,
                        Persona.FIELD_TECHNICIAN,
                        "Securely access the RoomScan mobile application.",
                        List.of("User authenticated", "RoomScan dashboard displayed"),
                        List.of("Authentication failure", "Expired session")),
                transaction("RS-02", "Create Scan Session", WorkflowStage.CREATE_SCAN_SESSION,
                        Persona.ROOMSCAN_MOBILE_APP,
                        "Create a traceable RoomScan session for a property.",
                        List.of("Scan ID generated", "Capture workflow initialized"),
                        List.of("Duplicate session", "Missing property metadata")),
                transaction("RS-03", "Capture Room Images", WorkflowStage.CAPTURE_IMAGES,
                        Persona.FIELD_TECHNICIAN,
                        "Capture sufficient, high-quality room evidence.",
                        List.of("Images captured", "Coverage and metadata validated"),
                        List.of("Low light", "Incomplete coverage", "Corrupt image")),
                transaction("RS-04", "Upload Room Images", WorkflowStage.UPLOAD_IMAGES,
                        Persona.ROOMSCAN_MOBILE_APP,
                        "Upload room images and metadata to the RoomScan backend.",
                        List.of("All images uploaded", "Upload manifest persisted"),
                        List.of("Timeout", "Bandwidth degradation", "Partial upload")),
                transaction("RS-05", "Submit AI Processing", WorkflowStage.SUBMIT_AI_PROCESSING,
                        Persona.AI_GATEWAY,
                        "Submit the scan to CubiCasa or another approved AI provider.",
                        List.of("AI job accepted", "Job ID generated"),
                        List.of("Provider unavailable", "Request rejected")),
                transaction("RS-06", "Poll AI Status", WorkflowStage.POLL_AI_STATUS,
                        Persona.ROOMSCAN_BACKEND,
                        "Track asynchronous AI processing without excessive polling.",
                        List.of("Status transitions valid", "Completion detected"),
                        List.of("Polling storm", "Job timeout", "Stale status")),
                transaction("RS-07", "Retrieve Floor Plan", WorkflowStage.RETRIEVE_FLOOR_PLAN,
                        Persona.ROOMSCAN_BACKEND,
                        "Retrieve the generated floor plan and measurements.",
                        List.of("Floor plan retrieved", "Rooms and measurements present"),
                        List.of("Missing result", "Invalid geometry", "Incomplete metadata")),
                transaction("RS-08", "Review AI Result", WorkflowStage.REVIEW_RESULTS,
                        Persona.QUALITY_ENGINEER,
                        "Review the AI-generated result for business and quality acceptance.",
                        List.of("Result reviewed", "Quality criteria satisfied"),
                        List.of("Low confidence", "Measurement discrepancy")),
                transaction("RS-09", "Submit Completed Scan", WorkflowStage.SUBMIT_SCAN,
                        Persona.FIELD_TECHNICIAN,
                        "Submit the approved RoomScan result.",
                        List.of("Scan submitted", "History updated"),
                        List.of("Submission failure", "Version conflict")),
                transaction("RS-10", "Update Executive Dashboard", WorkflowStage.UPDATE_DASHBOARD,
                        Persona.EXECUTIVE_DASHBOARD,
                        "Publish quality evidence and release indicators.",
                        List.of("Module summaries published", "Quality gates visible"),
                        List.of("Missing evidence", "Stale summary")),
                transaction("RS-11", "Recommend Release Readiness", WorkflowStage.RELEASE_DECISION,
                        Persona.RELEASE_MANAGER,
                        "Use verified evidence and governed AI analysis to recommend release readiness.",
                        List.of("Readiness score calculated", "Recommendation supported by evidence"),
                        List.of("Unverified AI claim", "Critical gate failure"))
        );

        return new BusinessScenario(
                "ROOMSCAN-E2E-001",
                "RoomScan End-to-End Quality Validation",
                OBJECTIVE,
                List.of(
                        Persona.FIELD_TECHNICIAN,
                        Persona.ROOMSCAN_MOBILE_APP,
                        Persona.ROOMSCAN_BACKEND,
                        Persona.AI_GATEWAY,
                        Persona.CUBICASA,
                        Persona.ROOMSCAN_PORTAL,
                        Persona.QUALITY_ENGINEER,
                        Persona.RELEASE_MANAGER
                ),
                new BusinessWorkflow("RoomScan Lifecycle", OBJECTIVE, transactions),
                List.of(
                        "Critical functional flow passes",
                        "Image upload success rate meets target",
                        "AI processing completes within SLA",
                        "No unrecovered locator failure",
                        "API contracts and business rules pass",
                        "Performance thresholds pass",
                        "Evidence is complete and traceable"
                )
        );
    }

    public static List<BusinessMetric> productionMetrics() {
        return List.of(
                new BusinessMetric("Room Scans", 500, "scans", "Planned RoomScan sessions"),
                new BusinessMetric("Images Uploaded", 1500, "images", "Three room images per scan"),
                new BusinessMetric("AI Jobs Submitted", 500, "jobs", "One AI job per scan"),
                new BusinessMetric("Status Polls", 6000, "requests", "Average twelve polls per AI job"),
                new BusinessMetric("Floor Plans Retrieved", 500, "plans", "One completed result per scan"),
                new BusinessMetric("Target Request Volume", 10000, "requests", "Production-like enterprise workload")
        );
    }

    public static ExecutiveOutcome readyOutcome() {
        return new ExecutiveOutcome(
                "PASS",
                96,
                "LOW",
                "READY FOR PRODUCTION",
                List.of(
                        "Mobile capture",
                        "Image upload",
                        "AI processing",
                        "Floor-plan retrieval",
                        "Web review",
                        "API quality",
                        "Performance scalability"
                ),
                List.of()
        );
    }

    private static BusinessTransaction transaction(
            String id,
            String name,
            WorkflowStage stage,
            Persona actor,
            String objective,
            List<String> outcomes,
            List<String> risks
    ) {
        return new BusinessTransaction(id, name, stage, actor, objective, outcomes, risks);
    }
}
