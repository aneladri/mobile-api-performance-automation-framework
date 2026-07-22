package core.ai;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgentTrendAnalyzer {

    public int averageConfidence(List<ExecutionRecord> records) {
        if (records.isEmpty()) {
            return 0;
        }

        return (int) records.stream()
                .mapToInt(ExecutionRecord::getConfidence)
                .average()
                .orElse(0);
    }

    public Map<String, Long> classificationCounts(List<ExecutionRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        ExecutionRecord::getClassification,
                        Collectors.counting()
                ));
    }
}
