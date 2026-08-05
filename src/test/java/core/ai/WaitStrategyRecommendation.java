package core.ai;

public class WaitStrategyRecommendation {

    private String failureType;
    private String suggestedWait;
    private String implementation;
    private String alternative;
    private int confidence;

    public String getFailureType() {
        return failureType;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public String getSuggestedWait() {
        return suggestedWait;
    }

    public void setSuggestedWait(String suggestedWait) {
        this.suggestedWait = suggestedWait;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public String getAlternative() {
        return alternative;
    }

    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
