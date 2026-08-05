package dashboard.enterprise.agent.llm.provider;

import dashboard.enterprise.agent.connector.credential.CredentialResolver;
import dashboard.enterprise.agent.connector.model.SecretReference;
import dashboard.enterprise.agent.llm.model.LlmModelConfiguration;
import dashboard.enterprise.agent.llm.model.LlmProviderType;
import dashboard.enterprise.agent.llm.model.LlmRequest;
import dashboard.enterprise.agent.llm.model.LlmResponse;
import dashboard.enterprise.agent.model.AgentContext;

import java.util.Map;
import java.util.Set;

public abstract class AbstractLiveLlmProvider implements LlmProvider {

    private final String id;
    private final LlmProviderType type;
    private final int priority;
    private final SecretReference credentialReference;
    private final CredentialResolver credentials;
    private final Set<String> capabilities;
    private final LlmModelConfiguration model;

    protected AbstractLiveLlmProvider(
            String id,
            LlmProviderType type,
            int priority,
            String credentialKey,
            CredentialResolver credentials,
            String modelId,
            Set<String> capabilities
    ) {
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.credentialReference = new SecretReference(
                "mapaf.connector.secret-reference/v1",
                id + "-credential",
                credentialKey
        );
        this.credentials = credentials;
        this.capabilities = Set.copyOf(capabilities);
        this.model = new LlmModelConfiguration(
                "mapaf.llm.model/v1",
                modelId,
                id,
                0.2,
                4096,
                30000,
                Map.of("mode", "LIVE_FOUNDATION")
        );
    }

    @Override
    public String providerId() {
        return id;
    }

    @Override
    public LlmProviderType providerType() {
        return type;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public boolean available() {
        return credentials.resolve(credentialReference).isPresent();
    }

    @Override
    public Set<String> capabilities() {
        return capabilities;
    }

    @Override
    public LlmModelConfiguration model() {
        return model;
    }

    @Override
    public LlmResponse generate(
            AgentContext context,
            LlmRequest request
    ) {
        if (!available()) {
            throw new IllegalStateException(
                    "LLM provider is not configured: " + id
            );
        }

        throw new UnsupportedOperationException(
                "Live network invocation is disabled in "
                        + "Phase 2.3 foundation for "
                        + id
                        + "."
        );
    }
}
