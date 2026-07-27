package common.reporting.dashboard.widget;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardWidget {

    private String id;
    private String title;
    private String subtitle;
    private WidgetType type;
    private WidgetStatus status;
    private int displayOrder;
    private boolean visible = true;

    private Map<String, Object> data = new LinkedHashMap<>();
    private Map<String, String> links = new LinkedHashMap<>();

    public DashboardWidget() {
    }

    public DashboardWidget(
            String id,
            String title,
            WidgetType type
    ) {
        setId(id);
        setTitle(title);
        setType(type);
        this.status = WidgetStatus.INFORMATION;
    }

    public void addData(String name, Object value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Widget data name must not be blank"
            );
        }

        data.put(name, value);
    }

    public void addLink(String name, String url) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Widget link name must not be blank"
            );
        }

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "Widget link URL must not be blank"
            );
        }

        links.put(name, url);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = requireText(id, "Widget ID");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = requireText(title, "Widget title");
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public WidgetType getType() {
        return type;
    }

    public void setType(WidgetType type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "Widget type must not be null"
            );
        }

        this.type = type;
    }

    public WidgetStatus getStatus() {
        return status;
    }

    public void setStatus(WidgetStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Widget status must not be null"
            );
        }

        this.status = status;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        if (displayOrder < 0) {
            throw new IllegalArgumentException(
                    "Widget display order must not be negative"
            );
        }

        this.displayOrder = displayOrder;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Map<String, Object> getData() {
        return new LinkedHashMap<>(data);
    }

    public void setData(Map<String, Object> data) {
        this.data = data == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(data);
    }

    public Map<String, String> getLinks() {
        return new LinkedHashMap<>(links);
    }

    public void setLinks(Map<String, String> links) {
        this.links = links == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(links);
    }

    private String requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value;
    }
}
