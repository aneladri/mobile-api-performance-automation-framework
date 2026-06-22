package core.ai;

import java.util.ArrayList;
import java.util.List;

public final class AgentExecutionHistory {

    private static final List<ExecutionRecord> RECORDS =
            new ArrayList<>();

    private AgentExecutionHistory() {
    }

    public static void record(ExecutionRecord record) {
        RECORDS.add(record);
    }

    public static List<ExecutionRecord> getRecords() {
        return RECORDS;
    }

    public static void reset() {
        RECORDS.clear();
    }
}
