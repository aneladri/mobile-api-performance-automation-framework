package common.reporting.model;

import java.time.Instant;

public class ExecutionEnvironment {

    private String environment;
    private String build;
    private String branch;
    private String commit;
    private String executionId;
    private String generatedAt;

    public ExecutionEnvironment() {
        this.generatedAt = Instant.now().toString();
    }

    public ExecutionEnvironment(
            String environment,
            String build,
            String branch,
            String commit,
            String executionId
    ) {
        this.environment = environment;
        this.build = build;
        this.branch = branch;
        this.commit = commit;
        this.executionId = executionId;
        this.generatedAt = Instant.now().toString();
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
