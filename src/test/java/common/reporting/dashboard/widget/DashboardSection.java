package common.reporting.dashboard.widget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardSection {

    private String id;
    private String title;
    private int displayOrder;
    private boolean visible = true;
    private List<DashboardWidget> widgets = new ArrayList<>();

    public DashboardSection() {
    }

    public DashboardSection(
            String id,
            String title,
            int displayOrder
    ) {
        setId(id);
        setTitle(title);
        setDisplayOrder(displayOrder);
    }

    public void addWidget(DashboardWidget widget) {
        if (widget == null) {
            throw new IllegalArgumentException(
                    "Dashboard widget must not be null"
            );
        }

        widgets.add(widget);
    }

    public List<DashboardWidget> getVisibleWidgets() {
        return widgets.stream()
                .filter(DashboardWidget::isVisible)
                .sorted(
                        Comparator.comparingInt(
                                DashboardWidget::getDisplayOrder
                        )
                )
                .toList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = requireText(id, "Section ID");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = requireText(title, "Section title");
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        if (displayOrder < 0) {
            throw new IllegalArgumentException(
                    "Section display order must not be negative"
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

    public List<DashboardWidget> getWidgets() {
        return new ArrayList<>(widgets);
    }

    public void setWidgets(List<DashboardWidget> widgets) {
        this.widgets = widgets == null
                ? new ArrayList<>()
                : new ArrayList<>(widgets);
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
