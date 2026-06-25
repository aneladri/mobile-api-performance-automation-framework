package core.ai.providers;

public interface AIProvider {

    AIResponse complete(AIRequest request);
}
