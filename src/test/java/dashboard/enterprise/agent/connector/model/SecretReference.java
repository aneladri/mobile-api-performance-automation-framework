package dashboard.enterprise.agent.connector.model;
public record SecretReference(String schemaVersion, String alias, String environmentVariable) {
    public SecretReference {
        schemaVersion = safe(schemaVersion); alias = required(alias, "Secret alias");
        environmentVariable = required(environmentVariable, "Environment variable");
    }
    private static String safe(String v) { return v == null ? "" : v.trim(); }
    private static String required(String v, String label) { String n=safe(v); if(n.isEmpty()) throw new IllegalArgumentException(label+" is required."); return n; }
}
