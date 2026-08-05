package dashboard.enterprise.agent.connector.credential;
import dashboard.enterprise.agent.connector.model.SecretReference;
import java.util.Map; import java.util.Optional;
public final class EnvironmentCredentialResolver implements CredentialResolver {
    private final Map<String,String> environment;
    public EnvironmentCredentialResolver(Map<String,String> environment){this.environment=environment==null?Map.of():Map.copyOf(environment);}
    public Optional<String> resolve(SecretReference reference){return Optional.ofNullable(environment.get(reference.environmentVariable())).filter(v->!v.isBlank());}
}
