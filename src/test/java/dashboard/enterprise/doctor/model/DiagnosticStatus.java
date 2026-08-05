package dashboard.enterprise.doctor.model;

/**
 * Status of an individual diagnostic check inside a health probe.
 */
public enum DiagnosticStatus {
    PASS,
    WARN,
    FAIL,
    SKIPPED
}
