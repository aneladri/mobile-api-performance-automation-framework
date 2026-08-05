package dashboard.enterprise.agent.integration.work.model;

import java.util.List;

public record WorkItem(
        String schemaVersion,
        String provider,
        String id,
        String key,
        String title,
        String description,
        List<String> acceptanceCriteria,
        String type,
        String status,
        String priority,
        List<String> labels,
        String assignee,
        String parentId,
        String sourceUrl
) {
    public WorkItem {
        schemaVersion = safe(schemaVersion);
        provider = required(provider, "Provider");
        id = required(id, "Work item id");
        key = safe(key);
        title = required(title, "Title");
        description = safe(description);
        acceptanceCriteria = acceptanceCriteria == null ? List.of() : List.copyOf(acceptanceCriteria);
        type = safe(type);
        status = safe(status);
        priority = safe(priority);
        labels = labels == null ? List.of() : List.copyOf(labels);
        assignee = safe(assignee);
        parentId = safe(parentId);
        sourceUrl = safe(sourceUrl);
    }
    private static String safe(String value) { return value == null ? "" : value.trim(); }
    private static String required(String value, String label) {
        String v = safe(value);
        if (v.isEmpty()) throw new IllegalArgumentException(label + " is required.");
        return v;
    }
}
