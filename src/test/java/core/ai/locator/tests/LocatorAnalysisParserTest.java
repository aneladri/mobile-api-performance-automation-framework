package core.ai.locator.tests;

import core.ai.locator.LocatorAnalysisParser;
import core.ai.locator.LocatorAnalysisResult;
import core.ai.locator.LocatorCandidate;
import core.ai.locator.LocatorStrategy;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class LocatorAnalysisParserTest {

        private final LocatorAnalysisParser parser = new LocatorAnalysisParser();

        @Test
        public void shouldParseCompleteLocatorAnalysis() {

                String response = """
                                ## Summary

                                Accessibility ID is the preferred locator.

                                ## Candidates

                                Strategy: ACCESSIBILITY_ID
                                Value: submit-order
                                Confidence: 97
                                Fallback: false
                                Reasoning: Stable accessibility identifier
                                """;

                LocatorAnalysisResult result = parser.parse(response);

                Assert.assertTrue(result.isSuccessful());

                Assert.assertEquals(
                                result.getSummary(),
                                "Accessibility ID is the preferred locator.");

                LocatorCandidate candidate = result.getBestCandidate();

                Assert.assertEquals(
                                candidate.getStrategy(),
                                LocatorStrategy.ACCESSIBILITY_ID);

                Assert.assertEquals(
                                candidate.getValue(),
                                "submit-order");

                Assert.assertEquals(
                                candidate.getConfidence(),
                                97);

                Assert.assertFalse(
                                candidate.isFallback());
        }

        @Test
        public void shouldParseMultipleLocatorCandidates() {

                String response = """
                                ## Summary

                                Multiple candidates found.

                                ## Candidates

                                Strategy: ACCESSIBILITY_ID
                                Value: submit-order
                                Confidence: 97
                                Fallback: false
                                Reasoning: Stable

                                Strategy: RESOURCE_ID
                                Value: com.demo:id/submit
                                Confidence: 90
                                Fallback: true
                                Reasoning: Native ID

                                Strategy: XPATH
                                Value: //button[@text='Submit']
                                Confidence: 55
                                Fallback: true
                                Reasoning: Dynamic locator
                                """;

                LocatorAnalysisResult result = parser.parse(response);

                Assert.assertEquals(
                                result.getCandidates().size(),
                                3);
        }

        @Test
        public void shouldIgnoreCaseForStrategyNames() {

                String response = """
                                ## Summary

                                Locator found.

                                ## Candidates

                                Strategy: accessibility_id
                                Value: submit-order
                                Confidence: 92
                                Fallback: FALSE
                                Reasoning: Stable
                                """;

                LocatorAnalysisResult result = parser.parse(response);

                Assert.assertEquals(
                                result.getBestCandidate().getStrategy(),
                                LocatorStrategy.ACCESSIBILITY_ID);
        }

        @Test
        public void shouldSupportMultilineReasoning() {

                String response = """
                                ## Summary

                                Locator found.

                                ## Candidates

                                Strategy: ID
                                Value: submit

                                Confidence: 80

                                Fallback: false

                                Reasoning: First line.
                                Second line.
                                Third line.
                                """;

                LocatorAnalysisResult result = parser.parse(response);

                Assert.assertTrue(
                                result.getBestCandidate()
                                                .getReasoning()
                                                .contains("Second line."));

                Assert.assertTrue(
                                result.getBestCandidate()
                                                .getReasoning()
                                                .contains("Third line."));
        }

        @Test
        public void shouldSkipInvalidCandidateAndKeepValidCandidate() {

                String response = """
                                ## Summary

                                Mixed response.

                                ## Candidates

                                Strategy: ID
                                Value:
                                Confidence: 90
                                Fallback: false

                                Strategy: ACCESSIBILITY_ID
                                Value: submit-order
                                Confidence: 98
                                Fallback: false
                                Reasoning: Stable
                                """;

                LocatorAnalysisResult result = parser.parse(response);

                Assert.assertTrue(result.isSuccessful());

                Assert.assertEquals(
                                result.getCandidates().size(),
                                1);

                Assert.assertEquals(
                                result.getBestCandidate().getStrategy(),
                                LocatorStrategy.ACCESSIBILITY_ID);
        }

        @Test
        public void shouldReturnFailureForEmptyResponse() {

                LocatorAnalysisResult result = parser.parse(" ");

                Assert.assertFalse(
                                result.isSuccessful());

                Assert.assertEquals(
                                result.getErrorMessage(),
                                "Locator analysis response was empty");
        }

        @Test
        public void shouldReturnFailureWhenCandidatesSectionIsMissing() {

                String response = """
                                ## Summary

                                Locator found.
                                """;

                LocatorAnalysisResult result = parser.parse(response);

                Assert.assertFalse(
                                result.isSuccessful());

                Assert.assertEquals(
                                result.getErrorMessage(),
                                "Locator analysis response is missing candidates");
        }

        @Test
        public void shouldReturnFailureWhenNoValidCandidateExists() {

                String response = """
                                ## Summary

                                Invalid.

                                ## Candidates

                                Strategy:
                                Value:
                                Confidence:
                                """;

                LocatorAnalysisResult result = parser.parse(response);

                Assert.assertFalse(
                                result.isSuccessful());

                Assert.assertEquals(
                                result.getErrorMessage(),
                                "Locator analysis response contains no valid candidates");
        }

        @Test
        public void shouldReturnImmutableCandidateList() {

                String response = """
                                ## Summary

                                Locator.

                                ## Candidates

                                Strategy: ID
                                Value: submit
                                Confidence: 90
                                Fallback: false
                                """;

                LocatorAnalysisResult result = parser.parse(response);

                List<LocatorCandidate> candidates = result.getCandidates();

                Assert.expectThrows(
                                UnsupportedOperationException.class,
                                () -> candidates.clear());
        }

        @Test
        public void parsesCurrentClaudeMarkdownCandidateFormat() {

                String response = """
                                ## Summary

                                The original locator does not exist.

                                ## Candidates

                                **Candidate 1:**
                                - Strategy: Accessibility ID (content-desc)
                                - Value: `Views`
                                - Confidence: 95%
                                - Fallback: Resource ID + Text combination
                                - Reasoning: The content description is stable and uniquely identifies the element.
                                """;

                LocatorAnalysisResult result = new LocatorAnalysisParser().parse(response);

                Assert.assertNotNull(result);
                Assert.assertEquals(result.getCandidates().size(), 1);

                LocatorCandidate candidate = result.getCandidates().get(0);

                Assert.assertEquals(
                                candidate.getStrategy(),
                                LocatorStrategy.ACCESSIBILITY_ID);

                Assert.assertEquals(
                                candidate.getValue(),
                                "Views");

                Assert.assertEquals(
                                candidate.getConfidence(),
                                95);
        }
}
