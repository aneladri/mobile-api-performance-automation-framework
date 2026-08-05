package core.enterprise.qualitygate;

public record QualityGateResult(String name, boolean passed, String expected,
                                String actual, String message) {}
