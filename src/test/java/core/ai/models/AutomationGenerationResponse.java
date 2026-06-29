package core.ai.models;

public class AutomationGenerationResponse {

    private final String content;

    public AutomationGenerationResponse(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }
}
