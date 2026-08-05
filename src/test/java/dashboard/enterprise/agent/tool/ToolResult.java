package dashboard.enterprise.agent.tool;

import java.util.List;
import java.util.Map;

public record ToolResult(
        boolean successful,
        String summary,
        List<String> evidence,
        Map<String, Object> data
) {
    public ToolResult {
        summary = summary == null ? "" : summary;
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        data = data == null ? Map.of() : Map.copyOf(data);
    }
}
