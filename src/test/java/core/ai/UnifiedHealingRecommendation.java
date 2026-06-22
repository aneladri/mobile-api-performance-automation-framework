package core.ai;

public class UnifiedHealingRecommendation {

    private String locatorRecommendation;
    private String waitRecommendation;
    private String configurationRecommendation;
    private int confidence;

    public String getLocatorRecommendation() {
        return locatorRecommendation;
    }

    public void setLocatorRecommendation(String locatorRecommendation) {
        this.locatorRecommendation = locatorRecommendation;
    }

    public String getWaitRecommendation() {
        return waitRecommendation;
    }

    public void setWaitRecommendation(String waitRecommendation) {
        this.waitRecommendation = waitRecommendation;
    }

    public String getConfigurationRecommendation() {
        return configurationRecommendation;
    }

    public void setConfigurationRecommendation(String configurationRecommendation) {
        this.configurationRecommendation = configurationRecommendation;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
