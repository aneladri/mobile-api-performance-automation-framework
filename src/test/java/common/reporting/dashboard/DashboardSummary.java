package common.reporting.dashboard;

import common.reporting.model.ExecutionEnvironment;
import common.reporting.model.ExecutionStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummary {

    private String contractVersion = "1.0";
    private String generatedAt = Instant.now().toString();
    private ExecutionStatus overallStatus = ExecutionStatus.NOT_RUN;
    private DashboardMetrics metrics = new DashboardMetrics();
    private ExecutionEnvironment environment;
    private List<DashboardModule> modules = new ArrayList<>();

    public DashboardSummary() {
    }

    public DashboardSummary(
            ExecutionStatus overallStatus,
            DashboardMetrics metrics,
            ExecutionEnvironment environment,
            List<DashboardModule> modules
    ) {
        this.overallStatus = overallStatus;
        this.metrics = metrics;
        this.environment = environment;
        this.modules = modules == null
                ? new ArrayList<>()
                : new ArrayList<>(modules);
    }

    public void addModule(DashboardModule module) {
        if (module == null) {
            throw new IllegalArgumentException(
                    "Dashboard module must not be null"
            );
        }

        modules.add(module);
    }

    public String getContractVersion() {
        return contractVersion;
    }

    public void setContractVersion(String contractVersion) {
        this.contractVersion = contractVersion;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public ExecutionStatus getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(ExecutionStatus overallStatus) {
        this.overallStatus = overallStatus;
    }

    public DashboardMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(DashboardMetrics metrics) {
        this.metrics = metrics;
    }

    public ExecutionEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(ExecutionEnvironment environment) {
        this.environment = environment;
    }

    public List<DashboardModule> getModules() {
        return modules;
    }

    public void setModules(List<DashboardModule> modules) {
        this.modules = modules == null
                ? new ArrayList<>()
                : new ArrayList<>(modules);
    }
}
