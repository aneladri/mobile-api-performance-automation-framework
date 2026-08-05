package common.reporting.dashboard.builder;

import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.widget.DashboardSection;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardPage {

    private DashboardConfiguration configuration;
    private String generatedAt = Instant.now().toString();
    private List<DashboardSection> sections = new ArrayList<>();

    public DashboardPage() {
    }

    public DashboardPage(DashboardConfiguration configuration) {
        setConfiguration(configuration);
    }

    public void addSection(DashboardSection section) {
        if (section == null) {
            throw new IllegalArgumentException(
                    "Dashboard section must not be null"
            );
        }

        sections.add(section);
    }

    public List<DashboardSection> getVisibleSections() {
        return sections.stream()
                .filter(DashboardSection::isVisible)
                .sorted(
                        Comparator.comparingInt(
                                DashboardSection::getDisplayOrder
                        )
                )
                .toList();
    }

    public DashboardConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(
            DashboardConfiguration configuration
    ) {
        if (configuration == null) {
            throw new IllegalArgumentException(
                    "Dashboard configuration must not be null"
            );
        }

        this.configuration = configuration;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        if (generatedAt == null || generatedAt.isBlank()) {
            throw new IllegalArgumentException(
                    "Dashboard generated time must not be blank"
            );
        }

        this.generatedAt = generatedAt;
    }

    public List<DashboardSection> getSections() {
        return new ArrayList<>(sections);
    }

    public void setSections(List<DashboardSection> sections) {
        this.sections = sections == null
                ? new ArrayList<>()
                : new ArrayList<>(sections);
    }
}
