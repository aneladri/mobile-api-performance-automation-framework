package core.enterprise.security;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class SensitiveDataMasker {
    private static final String MASK = "********";
    private static final Set<String> SENSITIVE_TERMS = Set.of(
            "authorization", "api-key", "apikey", "token", "secret", "password", "cookie", "session"
    );
    private SensitiveDataMasker() {}

    public static String mask(String name, String value) {
        if (value == null) return "";
        return isSensitive(name) ? MASK : value;
    }

    public static Map<String, String> mask(Map<String, String> values) {
        Map<String, String> masked = new LinkedHashMap<>();
        if (values != null) values.forEach((key, value) -> masked.put(key, mask(key, value)));
        return masked;
    }

    public static boolean isSensitive(String name) {
        if (name == null) return false;
        String normalized = name.toLowerCase(Locale.ROOT);
        return SENSITIVE_TERMS.stream().anyMatch(normalized::contains);
    }
}
