package dashboard.enterprise.agent.llm.provider;
import dashboard.enterprise.agent.connector.credential.CredentialResolver;import dashboard.enterprise.agent.llm.model.LlmProviderType;import java.util.Set;
public final class GeminiLlmProvider extends AbstractLiveLlmProvider {public GeminiLlmProvider(CredentialResolver credentials){super("Gemini".replaceAll("([a-z])([A-Z])","$1-$2").toLowerCase()+"-live",LlmProviderType.GEMINI,80,"GEMINI_API_KEY",credentials,"gemini-enterprise",Set.of("reasoning.generate","test-design.generate","summary.generate","release-recommendation.generate"));}}
