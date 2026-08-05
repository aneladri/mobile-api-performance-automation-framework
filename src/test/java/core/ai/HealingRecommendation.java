package core.ai;

public class HealingRecommendation {

    private String failureType;
    private String possibleCause;
    private String suggestedFix;
    private String suggestedStrategy;
    private int confidence;

    public String getFailureType() {
        return failureType;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public String getPossibleCause() {
        return possibleCause;
    }

    public void setPossibleCause(String possibleCause) {
        this.possibleCause = possibleCause;
    }

    public String getSuggestedFix() {
        return suggestedFix;
    }

    public void setSuggestedFix(String suggestedFix) {
        this.suggestedFix = suggestedFix;
    }

    public String getSuggestedStrategy() {
        return suggestedStrategy;
    }

    public void setSuggestedStrategy(String suggestedStrategy) {
        this.suggestedStrategy = suggestedStrategy;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
