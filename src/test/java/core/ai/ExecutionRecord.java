package core.ai;

public class ExecutionRecord {

    private final String agentName;
    private final String classification;
    private final int confidence;

    public ExecutionRecord(
            String agentName,
            String classification,
            int confidence
    ) {
        this.agentName = agentName;
        this.classification = classification;
        this.confidence = confidence;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getClassification() {
        return classification;
    }

    public int getConfidence() {
        return confidence;
    }
}
