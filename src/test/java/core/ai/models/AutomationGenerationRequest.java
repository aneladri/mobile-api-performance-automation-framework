package core.ai.models;

public class AutomationGenerationRequest {

    private final String userStory;
    private final String platform;
    private final String screenName;
    private final String acceptanceCriteria;
    private final String targetPackage;

    public AutomationGenerationRequest(
            String userStory,
            String platform,
            String screenName,
            String acceptanceCriteria,
            String targetPackage
    ) {
        this.userStory = userStory;
        this.platform = platform;
        this.screenName = screenName;
        this.acceptanceCriteria = acceptanceCriteria;
        this.targetPackage = targetPackage;
    }

    public String getUserStory() {
        return userStory;
    }

    public String getPlatform() {
        return platform;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public String getTargetPackage() {
        return targetPackage;
    }
}
