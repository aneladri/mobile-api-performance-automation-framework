package dashboard.enterprise.agent.integration.work.azure;

import dashboard.enterprise.agent.integration.work.model.WorkItem;
import java.util.List;
import java.util.Map;

public final class AzureBoardsWorkItemMapper {
    public WorkItem map(Map<String, Object> source) {
        return new WorkItem(
                "mapaf.work-item/v1",
                "AZURE_BOARDS",
                value(source, "id"),
                value(source, "key"),
                value(source, "title"),
                value(source, "description"),
                list(source.get("acceptanceCriteria")),
                value(source, "type"),
                value(source, "status"),
                value(source, "priority"),
                list(source.get("labels")),
                value(source, "assignee"),
                value(source, "parentId"),
                value(source, "sourceUrl")
        );
    }
    private static String value(Map<String, Object> source, String key) {
        Object v = source.get(key); return v == null ? "" : String.valueOf(v);
    }
    @SuppressWarnings("unchecked")
    private static List<String> list(Object value) {
        return value instanceof List<?> list ? list.stream().map(String::valueOf).toList() : List.of();
    }
}
