package dashboard.enterprise.agent.llm.provider;
import dashboard.enterprise.agent.connector.credential.CredentialResolver;import dashboard.enterprise.agent.llm.model.LlmProviderType;import java.util.Set;
public final class LocalLlmProvider extends AbstractLiveLlmProvider {public LocalLlmProvider(CredentialResolver credentials){super("Local".replaceAll("([a-z])([A-Z])","$1-$2").toLowerCase()+"-live",LlmProviderType.LOCAL,80,"LOCAL_LLM_ENABLED",credentials,"local-model",Set.of("reasoning.generate","test-design.generate","summary.generate","release-recommendation.generate"));}}
