package core.enterprise.execution;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public final class ExecutionIdentityFactory {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private ExecutionIdentityFactory() {}

    public static EnterpriseExecutionContext create(String module, String scenario) {
        String normalized = module.trim().toUpperCase(Locale.ROOT);
        return new EnterpriseExecutionContext(
                module,
                scenario,
                normalized + "-" + LocalDateTime.now().format(FORMATTER),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString().replace("-", "")
        );
    }
}
