package core.ai;

import java.util.List;

public class AgentHistoryReportGenerator {

    public String generate(List<ExecutionRecord> records) {

        StringBuilder report = new StringBuilder();

        report.append("# AI Agent Execution History Report\n\n");

        report.append("Total Executions:\n");
        report.append(records.size()).append("\n\n");

        report.append("## Execution Records\n\n");

        for (ExecutionRecord record : records) {
            report.append("- Agent: ")
                    .append(record.getAgentName())
                    .append("\n");

            report.append("  Classification: ")
                    .append(record.getClassification())
                    .append("\n");

            report.append("  Confidence: ")
                    .append(record.getConfidence())
                    .append("\n\n");
        }

        return report.toString();
    }
}
