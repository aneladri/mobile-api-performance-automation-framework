package core.ai;

public class LocatorRecommendation {

    private String failureType;
    private String suggestedLocator;
    private String alternativeLocator;
    private String suggestedWait;
    private int confidence;

    public String getFailureType() {
        return failureType;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public String getSuggestedLocator() {
        return suggestedLocator;
    }

    public void setSuggestedLocator(String suggestedLocator) {
        this.suggestedLocator = suggestedLocator;
    }

    public String getAlternativeLocator() {
        return alternativeLocator;
    }

    public void setAlternativeLocator(String alternativeLocator) {
        this.alternativeLocator = alternativeLocator;
    }

    public String getSuggestedWait() {
        return suggestedWait;
    }

    public void setSuggestedWait(String suggestedWait) {
        this.suggestedWait = suggestedWait;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
