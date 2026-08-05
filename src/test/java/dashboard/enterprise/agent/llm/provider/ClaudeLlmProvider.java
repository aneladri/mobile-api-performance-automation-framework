package dashboard.enterprise.agent.llm.provider;
import dashboard.enterprise.agent.connector.credential.CredentialResolver;import dashboard.enterprise.agent.llm.model.LlmProviderType;import java.util.Set;
public final class ClaudeLlmProvider extends AbstractLiveLlmProvider {public ClaudeLlmProvider(CredentialResolver credentials){super("Claude".replaceAll("([a-z])([A-Z])","$1-$2").toLowerCase()+"-live",LlmProviderType.CLAUDE,80,"ANTHROPIC_API_KEY",credentials,"claude-enterprise",Set.of("reasoning.generate","test-design.generate","summary.generate","release-recommendation.generate"));}}
