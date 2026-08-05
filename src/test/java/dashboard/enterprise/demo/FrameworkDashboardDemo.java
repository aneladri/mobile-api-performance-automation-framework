package dashboard.enterprise.demo;

import dashboard.enterprise.aggregation.EnterpriseSummaryAdapter;
import dashboard.enterprise.aggregation.ExecutiveDashboardAggregator;
import dashboard.enterprise.details.ExecutionDetailsHtmlWriter;
import dashboard.enterprise.model.EnterpriseModuleView;
import dashboard.enterprise.model.ExecutiveDashboardView;
import dashboard.enterprise.reporting.ExecutiveDashboardHtmlWriter;
import dashboard.enterprise.reporting.ExecutiveDashboardJsonWriter;

import java.nio.file.Path;
import java.util.List;

public final class FrameworkDashboardDemo {

    private FrameworkDashboardDemo() {
    }

    public static void main(String[] args) throws Exception {
        EnterpriseSummaryAdapter adapter = new EnterpriseSummaryAdapter();
        List<EnterpriseModuleView> modules = List.of(
                adapter.read("mobile", "RoomScan Mobile", Path.of("mobile/reports/enterprise-summary.json")),
                adapter.read("web", "RoomScan Portal", Path.of("web/reports/enterprise-summary.json")),
                adapter.read("api", "RoomScan Backend API", Path.of("api/reports/enterprise-summary.json")),
                adapter.read("performance", "RoomScan Performance", Path.of("performance/reports/enterprise-summary.json"))
        );

        ExecutionDetailsHtmlWriter detailsWriter = new ExecutionDetailsHtmlWriter();
        detailsWriter.write("mobile", "RoomScan Mobile", Path.of("mobile/reports/enterprise-summary.json"), Path.of("dashboard/reports/mobile.html"));
        detailsWriter.write("web", "RoomScan Portal", Path.of("web/reports/enterprise-summary.json"), Path.of("dashboard/reports/web.html"));
        detailsWriter.write("api", "RoomScan Backend API", Path.of("api/reports/enterprise-summary.json"), Path.of("dashboard/reports/api.html"));
        detailsWriter.write("performance", "RoomScan Performance", Path.of("performance/reports/enterprise-summary.json"), Path.of("dashboard/reports/performance.html"));

        ExecutiveDashboardView view = new ExecutiveDashboardAggregator().aggregate(modules);
        Path html = new ExecutiveDashboardHtmlWriter().write(view, Path.of("dashboard/reports/index.html"));
        Path json = new ExecutiveDashboardJsonWriter().write(view, Path.of("dashboard/reports/executive-summary.json"));

        print(view, html, json);
    }

    private static void print(ExecutiveDashboardView view, Path html, Path json) {
        System.out.println("======================================================================");
        System.out.println("        MAPAF ENTERPRISE QUALITY COMMAND CENTER");
        System.out.println("======================================================================");
        System.out.printf("%-30s : %s%n", "Application", view.application());
        System.out.printf("%-30s : %s%n", "Environment", view.environment());
        System.out.printf("%-30s : %d%%%n", "Release Readiness", view.readinessScore());
        System.out.printf("%-30s : %s%n", "Business Risk", view.risk());
        System.out.printf("%-30s : %s%n", "Recommendation", view.recommendation());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Module Health");
        for (EnterpriseModuleView module : view.modules()) {
            System.out.printf("%-30s : %s%n", module.name(), module.status());
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-30s : %s%n", "Executive Dashboard", html);
        System.out.printf("%-30s : %s%n", "Executive Summary", json);
        System.out.println("======================================================================");
    }
}
