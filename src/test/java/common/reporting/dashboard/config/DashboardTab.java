package common.reporting.dashboard.config;

public enum DashboardTab {

    OVERVIEW("Overview"),
    API("API"),
    WEB("Web"),
    PERFORMANCE("Performance"),
    ENVIRONMENT("Environment"),
    DOWNLOADS("Downloads");

    private final String displayName;

    DashboardTab(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}