package common.reporting.dashboard.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardConfiguration {

    private String title = "MAPAF Quality Engineering Dashboard";
    private String productName = "MAPAF";
    private String version = "2.0";
    private String dateTimeFormat = "dd-MMM-yyyy HH:mm:ss";
    private String reportFileName = "index.html";
    private DashboardTheme theme = DashboardTheme.LIGHT;

    private List<DashboardTab> enabledTabs = new ArrayList<>(Arrays.asList(
            DashboardTab.OVERVIEW,
            DashboardTab.API,
            DashboardTab.PERFORMANCE,
            DashboardTab.ENVIRONMENT,
            DashboardTab.DOWNLOADS));

    public static DashboardConfiguration defaultConfiguration() {
        return new DashboardConfiguration();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = requireText(title, "Dashboard title");
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = requireText(
                productName,
                "Product name");
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = requireText(version, "Version");
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = requireText(
                dateTimeFormat,
                "Date-time format");
    }

    public String getReportFileName() {
        return reportFileName;
    }

    public void setReportFileName(String reportFileName) {
        this.reportFileName = requireText(
                reportFileName,
                "Report filename");
    }

    public DashboardTheme getTheme() {
        return theme;
    }

    public void setTheme(DashboardTheme theme) {
        if (theme == null) {
            throw new IllegalArgumentException(
                    "Dashboard theme must not be null");
        }

        this.theme = theme;
    }

    public List<DashboardTab> getEnabledTabs() {
        return new ArrayList<>(enabledTabs);
    }

    public void setEnabledTabs(List<DashboardTab> enabledTabs) {
        if (enabledTabs == null || enabledTabs.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one dashboard tab must be enabled");
        }

        boolean containsNull = enabledTabs.stream()
                .anyMatch(tab -> tab == null);

        if (containsNull) {
            throw new IllegalArgumentException(
                    "Dashboard tabs must not contain null");
        }

        this.enabledTabs = new ArrayList<>(enabledTabs);
    }

    public boolean isTabEnabled(DashboardTab tab) {
        return tab != null && enabledTabs.contains(tab);
    }

    private String requireText(
            String value,
            String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }

        return value;
    }
}
