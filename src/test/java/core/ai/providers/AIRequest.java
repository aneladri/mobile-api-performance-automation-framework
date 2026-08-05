package core.ai.providers;

public class AIRequest {

    private final String prompt;
    private final String systemMessage;

    public AIRequest(
            String systemMessage,
            String prompt
    ) {
        this.systemMessage = systemMessage;
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getSystemMessage() {
        return systemMessage;
    }
}
