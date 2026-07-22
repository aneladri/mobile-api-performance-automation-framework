package ai.tests;

import core.locator.discovery.AndroidHierarchyLocatorDiscovery;
import core.locator.discovery.LocatorCandidate;
import core.locator.discovery.LocatorPromptFormatter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;

public class AndroidHierarchyLocatorDiscoveryTest {

    @Test
    public void discoversUniqueAccessibilityLocatorsFromExistingHierarchy() {
        List<LocatorCandidate> candidates =
                new AndroidHierarchyLocatorDiscovery()
                        .discover(Path.of("window_dump.xml"));

        Assert.assertTrue(
                candidates.stream().anyMatch(candidate ->
                        candidate.locatorType().equals("ACCESSIBILITY_ID")
                                && candidate.locatorValue().equals("Views")
                ),
                "Expected the Views accessibility locator to be discovered"
        );
    }

    @Test
    public void formatsVerifiedCandidatesForTheGeneratorPrompt() {
        String context = LocatorPromptFormatter.format(
                List.of(new LocatorCandidate(
                        "loginButton",
                        "android.widget.Button",
                        "ACCESSIBILITY_ID",
                        "login_button",
                        95,
                        true,
                        "test.xml"
                ))
        );

        Assert.assertTrue(context.contains("locatorValue=login_button"));
        Assert.assertTrue(context.contains("Do not invent locator values"));
    }
}
