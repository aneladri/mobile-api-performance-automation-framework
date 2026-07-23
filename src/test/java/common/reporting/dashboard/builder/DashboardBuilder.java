package common.reporting.dashboard.builder;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.config.DashboardTab;
import common.reporting.dashboard.section.DashboardSectionBuilder;
import common.reporting.dashboard.section.OverviewSectionBuilder;
import common.reporting.dashboard.section.ApiSectionBuilder;
import common.reporting.dashboard.section.PerformanceSectionBuilder;

import java.util.ArrayList;
import java.util.List;

public class DashboardBuilder {

        private final List<DashboardSectionBuilder> sectionBuilders = new ArrayList<>();

        public DashboardBuilder() {
                register(new OverviewSectionBuilder());
                register(new ApiSectionBuilder());
		register(new PerformanceSectionBuilder());
        }

        public DashboardBuilder register(
                        DashboardSectionBuilder builder) {
                if (builder == null) {
                        throw new IllegalArgumentException(
                                        "Dashboard section builder must not be null");
                }

                boolean duplicate = sectionBuilders.stream()
                                .anyMatch(existing -> existing.getSectionId().equalsIgnoreCase(
                                                builder.getSectionId()));

                if (duplicate) {
                        throw new IllegalArgumentException(
                                        "Dashboard section builder already registered: "
                                                        + builder.getSectionId());
                }

                sectionBuilders.add(builder);

                return this;
        }

        public DashboardPage build(
                        DashboardSummary summary,
                        DashboardConfiguration configuration) {
                if (summary == null) {
                        throw new IllegalArgumentException(
                                        "Dashboard summary must not be null");
                }

                if (configuration == null) {
                        throw new IllegalArgumentException(
                                        "Dashboard configuration must not be null");
                }

                DashboardPage page = new DashboardPage(configuration);
                page.setGeneratedAt(summary.getGeneratedAt());

                for (DashboardSectionBuilder builder : sectionBuilders) {
                        if (isEnabled(
                                        builder.getSectionId(),
                                        configuration)) {
                                page.addSection(builder.build(summary));
                        }
                }

                return page;
        }

        private boolean isEnabled(
                        String sectionId,
                        DashboardConfiguration configuration) {
                if (sectionId == null) {
                        return false;
                }

                return switch (sectionId.toLowerCase()) {
                        case "overview" ->
                                configuration.isTabEnabled(
                                                DashboardTab.OVERVIEW);

                        case "api" ->
                                configuration.isTabEnabled(
                                                DashboardTab.API);

                        case "performance" ->
                                configuration.isTabEnabled(
                                                DashboardTab.PERFORMANCE);

                        case "environment" ->
                                configuration.isTabEnabled(
                                                DashboardTab.ENVIRONMENT);

                        case "downloads" ->
                                configuration.isTabEnabled(
                                                DashboardTab.DOWNLOADS);

                        default -> true;
                };
        }
}
