package common.reporting.dashboard;

import common.reporting.model.ExecutionStatus;
import common.reporting.model.ExecutionSummary;

public class DashboardModule {

    private String module;
    private ExecutionStatus status;
    private ExecutionSummary summary;

    public DashboardModule() {
    }

    public DashboardModule(ExecutionSummary summary) {
        this.summary = summary;

        if (summary != null) {
            this.module = summary.getModule();
            this.status = summary.getStatus();
        }
    }

    public DashboardModule(
            String module,
            ExecutionStatus status,
            ExecutionSummary summary
    ) {
        this.module = module;
        this.status = status;
        this.summary = summary;
    }

    public static DashboardModule fromSummary(ExecutionSummary summary) {
        if (summary == null) {
            throw new IllegalArgumentException(
                    "Execution summary must not be null"
            );
        }

        return new DashboardModule(summary);
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public ExecutionSummary getSummary() {
        return summary;
    }

    public void setSummary(ExecutionSummary summary) {
        this.summary = summary;

        if (summary != null) {
            this.module = summary.getModule();
            this.status = summary.getStatus();
        }
    }
}
