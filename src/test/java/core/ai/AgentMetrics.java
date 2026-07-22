package core.ai;

public class AgentMetrics {

    private int totalRuns;
    private int successfulRuns;
    private int unknownClassifications;
    private int totalConfidence;

    public void recordRun(int confidence, boolean unknown) {
        totalRuns++;
        totalConfidence += confidence;

        if (unknown) {
            unknownClassifications++;
        } else {
            successfulRuns++;
        }
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public int getSuccessfulRuns() {
        return successfulRuns;
    }

    public int getUnknownClassifications() {
        return unknownClassifications;
    }

    public int getAverageConfidence() {
        if (totalRuns == 0) {
            return 0;
        }
        return totalConfidence / totalRuns;
    }
}
