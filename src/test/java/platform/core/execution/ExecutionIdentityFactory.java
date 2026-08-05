package platform.core.execution;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/** Generates portable execution, correlation and trace identifiers. */
public final class ExecutionIdentityFactory {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC);

    private ExecutionIdentityFactory() {
    }

    public static ExecutionContext create() {
        Instant now = Instant.now();
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new ExecutionContext(
                "RUN-" + FORMATTER.format(now) + "-" + suffix,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString().replace("-", ""),
                now
        );
    }
}
