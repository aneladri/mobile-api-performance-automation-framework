package dashboard.enterprise.agent.connector.health;
import dashboard.enterprise.agent.connector.credential.CredentialResolver;
import dashboard.enterprise.agent.connector.model.*;
import java.util.ArrayList; import java.util.List;
public final class ConnectorHealthChecker {
    private final CredentialResolver credentials;
    public ConnectorHealthChecker(CredentialResolver credentials){this.credentials=credentials;}
    public ConnectorHealth assess(ConnectorDefinition definition, ConnectorConnection connection){
        if(definition.mode()==ConnectorMode.REPLAY) return new ConnectorHealth("mapaf.connector.health/v1",definition.connectorId(),ConnectorStatus.HEALTHY,"Replay connector ready.",List.of(connection.endpoint()));
        List<String> missing=new ArrayList<>(); for(SecretReference s:definition.secrets()) if(credentials.resolve(s).isEmpty()) missing.add(s.alias());
        if(connection.endpoint().isBlank()) missing.add("endpoint");
        if(!missing.isEmpty()) return new ConnectorHealth("mapaf.connector.health/v1",definition.connectorId(),ConnectorStatus.NOT_CONFIGURED,"Live connector is not configured.",missing);
        return new ConnectorHealth("mapaf.connector.health/v1",definition.connectorId(),ConnectorStatus.HEALTHY,"Live connector configuration is available.",List.of(connection.endpoint()));
    }
}
