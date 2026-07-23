package common.reporting.tests;

import common.reporting.dashboard.builder.DashboardPage;
import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.renderer.DashboardHtmlRenderer;
import common.reporting.dashboard.widget.DashboardSection;
import common.reporting.dashboard.widget.DashboardWidget;
import common.reporting.dashboard.widget.WidgetStatus;
import common.reporting.dashboard.widget.WidgetType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class DashboardHtmlRendererTest {

    @Test
    public void shouldRenderDashboardPageToHtml() {
        DashboardPage page = createDashboardPage();

        String html = new DashboardHtmlRenderer()
                .renderToString(page);

        Assert.assertTrue(
                html.contains("<!DOCTYPE html>")
        );

        Assert.assertTrue(
                html.contains(
                        "MAPAF Quality Engineering Dashboard"
                )
        );

        Assert.assertTrue(
                html.contains("Overall Status")
        );

        Assert.assertTrue(
                html.contains("PASS")
        );

        Assert.assertTrue(
                html.contains("data-target=\"overview\"")
        );
    }

    @Test
    public void shouldWriteDashboardHtmlFile() throws Exception {
        DashboardPage page = createDashboardPage();

        Path outputFile = Files.createTempDirectory(
                "mapaf-dashboard-renderer"
        ).resolve("reports/index.html");

        Path renderedFile = new DashboardHtmlRenderer().render(
                page,
                outputFile
        );

        Assert.assertTrue(Files.exists(renderedFile));

        String html = Files.readString(renderedFile);

        Assert.assertTrue(
                html.contains("MAPAF")
        );
    }

    @Test
    public void shouldEscapeHtmlContent() {
        DashboardPage page = new DashboardPage(
                DashboardConfiguration.defaultConfiguration()
        );

        DashboardSection section = new DashboardSection(
                "overview",
                "<Overview>",
                1
        );

        DashboardWidget widget = new DashboardWidget(
                "unsafe",
                "Unsafe <script>",
                WidgetType.TEXT
        );

        widget.addData(
                "value",
                "<script>alert('test')</script>"
        );

        section.addWidget(widget);
        page.addSection(section);

        String html = new DashboardHtmlRenderer()
                .renderToString(page);

        Assert.assertFalse(
                html.contains("<script>alert('test')</script>")
        );

        Assert.assertTrue(
                html.contains(
                        "&lt;script&gt;alert(&#39;test&#39;)&lt;/script&gt;"
                )
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard page must not be null"
    )
    public void shouldRejectNullDashboardPage() {
        new DashboardHtmlRenderer().renderToString(null);
    }

    private DashboardPage createDashboardPage() {
        DashboardPage page = new DashboardPage(
                DashboardConfiguration.defaultConfiguration()
        );

        DashboardSection section = new DashboardSection(
                "overview",
                "Overview",
                1
        );

        DashboardWidget status = new DashboardWidget(
                "overall-status",
                "Overall Status",
                WidgetType.STATUS
        );

        status.setStatus(WidgetStatus.SUCCESS);
        status.setDisplayOrder(1);
        status.addData("value", "PASS");

        section.addWidget(status);
        page.addSection(section);

        return page;
    }
}
