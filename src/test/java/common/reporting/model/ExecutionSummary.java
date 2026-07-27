package common.reporting.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExecutionSummary {

    private String contractVersion = "1.0";
    private String module;
    private ExecutionStatus status;
    private ExecutionMetrics metrics;
    private ExecutionEnvironment environment;
    private Map<String, Object> details = new LinkedHashMap<>();
    private Map<String, String> links = new LinkedHashMap<>();

    public ExecutionSummary() {
    }

    public ExecutionSummary(
            String module,
            ExecutionStatus status,
            ExecutionMetrics metrics,
            ExecutionEnvironment environment
    ) {
        this.module = module;
        this.status = status;
        this.metrics = metrics;
        this.environment = environment;
    }

    public String getContractVersion() {
        return contractVersion;
    }

    public void setContractVersion(String contractVersion) {
        this.contractVersion = contractVersion;
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

    public ExecutionMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(ExecutionMetrics metrics) {
        this.metrics = metrics;
    }

    public ExecutionEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(ExecutionEnvironment environment) {
        this.environment = environment;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public void addDetail(String name, Object value) {
        details.put(name, value);
    }

    public void addLink(String name, String value) {
        links.put(name, value);
    }
}
