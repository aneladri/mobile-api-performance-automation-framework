package core.ai;

public class ConfigurationRecommendation {

    private String failureType;
    private String missingConfiguration;
    private String suggestedFix;
    private String validationCommand;
    private int confidence;

    public String getFailureType() {
        return failureType;
    }

    public void setFailureType(String failureType) {
        this.failureType = failureType;
    }

    public String getMissingConfiguration() {
        return missingConfiguration;
    }

    public void setMissingConfiguration(String missingConfiguration) {
        this.missingConfiguration = missingConfiguration;
    }

    public String getSuggestedFix() {
        return suggestedFix;
    }

    public void setSuggestedFix(String suggestedFix) {
        this.suggestedFix = suggestedFix;
    }

    public String getValidationCommand() {
        return validationCommand;
    }

    public void setValidationCommand(String validationCommand) {
        this.validationCommand = validationCommand;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
}
