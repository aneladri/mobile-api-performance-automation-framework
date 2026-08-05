package dashboard.enterprise.agent.connector.audit;
import com.fasterxml.jackson.databind.ObjectMapper; import core.json.JsonMapper;
import dashboard.enterprise.agent.connector.model.ConnectorAuditEvent;
import java.nio.file.Files; import java.nio.file.Path; import java.util.List;
public final class ConnectorAuditPublisher { private static final ObjectMapper MAPPER=JsonMapper.getInstance(); private ConnectorAuditPublisher(){}
    public static Path publish(Path root, List<ConnectorAuditEvent> events) throws Exception { Path out=root.resolve("dashboard/reports/connectors/connector-audit.json"); Files.createDirectories(out.getParent()); MAPPER.writerWithDefaultPrettyPrinter().writeValue(out.toFile(),events); return out; }
}
