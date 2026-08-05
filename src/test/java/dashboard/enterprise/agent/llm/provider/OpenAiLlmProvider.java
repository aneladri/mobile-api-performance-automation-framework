package dashboard.enterprise.agent.llm.provider;
import dashboard.enterprise.agent.connector.credential.CredentialResolver;import dashboard.enterprise.agent.llm.model.LlmProviderType;import java.util.Set;
public final class OpenAiLlmProvider extends AbstractLiveLlmProvider {public OpenAiLlmProvider(CredentialResolver credentials){super("OpenAi".replaceAll("([a-z])([A-Z])","$1-$2").toLowerCase()+"-live",LlmProviderType.OPENAI,80,"OPENAI_API_KEY",credentials,"gpt-4.1",Set.of("reasoning.generate","test-design.generate","summary.generate","release-recommendation.generate"));}}
