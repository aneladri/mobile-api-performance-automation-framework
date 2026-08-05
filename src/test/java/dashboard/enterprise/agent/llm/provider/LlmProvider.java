package dashboard.enterprise.agent.llm.provider;
import dashboard.enterprise.agent.llm.model.*;import dashboard.enterprise.agent.model.AgentContext;import java.util.Set;
public interface LlmProvider { String SCHEMA_VERSION="mapaf.llm.provider/v1";String providerId();LlmProviderType providerType();int priority();boolean available();Set<String> capabilities();LlmModelConfiguration model();LlmResponse generate(AgentContext context,LlmRequest request)throws Exception;default boolean supports(String capabilityId){return capabilities().contains(capabilityId);} }
