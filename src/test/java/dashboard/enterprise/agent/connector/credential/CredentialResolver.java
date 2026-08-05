package dashboard.enterprise.agent.connector.credential;
import dashboard.enterprise.agent.connector.model.SecretReference;
import java.util.Optional;
public interface CredentialResolver { Optional<String> resolve(SecretReference reference); }
