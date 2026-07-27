package common.reporting.dashboard.section;

import common.reporting.dashboard.DashboardSummary;
import common.reporting.dashboard.widget.DashboardSection;

public interface DashboardSectionBuilder {

    String getSectionId();

    int getDisplayOrder();

    DashboardSection build(DashboardSummary summary);
}
