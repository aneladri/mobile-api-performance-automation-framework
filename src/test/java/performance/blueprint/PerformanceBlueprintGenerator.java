package performance.blueprint;

import performance.models.PerformanceProfileFactory;
import performance.models.PerformanceScenario;
import performance.models.PerformanceScenarioFactory;

import java.util.ArrayList;
import java.util.List;

public class PerformanceBlueprintGenerator {

    public PerformanceBlueprint generateFromPostmanCollection(
            String collectionName
    ) {
        List<PerformanceScenario> scenarios =
                new ArrayList<>();

        scenarios.add(
                PerformanceScenarioFactory.apiHealth()
        );

        scenarios.add(
                PerformanceScenarioFactory.login()
        );

        scenarios.add(
                PerformanceScenarioFactory.uploadScan()
        );

        scenarios.add(
                PerformanceScenarioFactory.processScan()
        );

        return new PerformanceBlueprint(
                collectionName,
                "POSTMAN_COLLECTION",
                scenarios,
                List.of(
                        PerformanceProfileFactory.smoke(),
                        PerformanceProfileFactory.load()
                ),
                List.of(
                        "p95 response time should remain within agreed SLA",
                        "error rate should remain below 5%",
                        "throughput should not regress below baseline"
                ),
                List.of(
                        "Postman functional tests may require parameterization for load execution",
                        "Authentication and dynamic tokens must be handled before JMeter execution",
                        "Uploaded scan payloads may require test data isolation"
                )
        );
    }
}
