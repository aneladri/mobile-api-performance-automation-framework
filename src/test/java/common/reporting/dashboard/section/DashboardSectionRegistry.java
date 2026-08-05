package common.reporting.dashboard.section;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardSectionRegistry {

    private final List<DashboardSectionBuilder> builders =
            new ArrayList<>();

    public DashboardSectionRegistry register(
            DashboardSectionBuilder builder
    ) {
        if (builder == null) {
            throw new IllegalArgumentException(
                    "Dashboard section builder must not be null"
            );
        }

        boolean duplicate = builders.stream()
                .anyMatch(existing ->
                        existing.getSectionId().equalsIgnoreCase(
                                builder.getSectionId()
                        )
                );

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Dashboard section builder already registered: "
                            + builder.getSectionId()
            );
        }

        builders.add(builder);

        return this;
    }

    public List<DashboardSectionBuilder> getBuilders() {
        return builders.stream()
                .sorted(
                        Comparator.comparingInt(
                                DashboardSectionBuilder::getDisplayOrder
                        )
                )
                .toList();
    }

    public boolean contains(String sectionId) {
        if (sectionId == null || sectionId.isBlank()) {
            return false;
        }

        return builders.stream()
                .anyMatch(builder ->
                        builder.getSectionId()
                                .equalsIgnoreCase(sectionId)
                );
    }

    public int size() {
        return builders.size();
    }
}
