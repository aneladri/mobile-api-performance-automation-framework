package common.reporting.tests;

import common.reporting.dashboard.config.DashboardConfiguration;
import common.reporting.dashboard.config.DashboardTab;
import common.reporting.dashboard.config.DashboardTheme;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class DashboardConfigurationTest {

    @Test
    public void shouldCreateDefaultDashboardConfiguration() {
        DashboardConfiguration configuration =
                DashboardConfiguration.defaultConfiguration();

        Assert.assertEquals(
                configuration.getTitle(),
                "MAPAF Quality Engineering Dashboard"
        );

        Assert.assertEquals(
                configuration.getTheme(),
                DashboardTheme.LIGHT
        );

        Assert.assertTrue(
                configuration.isTabEnabled(
                        DashboardTab.OVERVIEW
                )
        );

        Assert.assertTrue(
                configuration.isTabEnabled(
                        DashboardTab.API
                )
        );

        Assert.assertTrue(
                configuration.isTabEnabled(
                        DashboardTab.PERFORMANCE
                )
        );
    }

    @Test
    public void shouldAllowDashboardCustomization() {
        DashboardConfiguration configuration =
                DashboardConfiguration.defaultConfiguration();

        configuration.setTitle("Custom Quality Dashboard");
        configuration.setVersion("2.1");
        configuration.setTheme(DashboardTheme.DARK);
        configuration.setEnabledTabs(
                List.of(
                        DashboardTab.OVERVIEW,
                        DashboardTab.API
                )
        );

        Assert.assertEquals(
                configuration.getTitle(),
                "Custom Quality Dashboard"
        );

        Assert.assertEquals(
                configuration.getVersion(),
                "2.1"
        );

        Assert.assertEquals(
                configuration.getTheme(),
                DashboardTheme.DARK
        );

        Assert.assertTrue(
                configuration.isTabEnabled(DashboardTab.API)
        );

        Assert.assertFalse(
                configuration.isTabEnabled(
                        DashboardTab.PERFORMANCE
                )
        );
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "At least one dashboard tab must be enabled"
    )
    public void shouldRejectEmptyDashboardTabs() {
        DashboardConfiguration configuration =
                DashboardConfiguration.defaultConfiguration();

        configuration.setEnabledTabs(List.of());
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Dashboard title must not be blank"
    )
    public void shouldRejectBlankDashboardTitle() {
        DashboardConfiguration configuration =
                DashboardConfiguration.defaultConfiguration();

        configuration.setTitle(" ");
    }
}
