package dashboard.enterprise.doctor.probe.environment;

import java.time.Duration;
import java.util.List;

public interface CommandExecutor {

    CommandResult execute(
            List<String> command,
            Duration timeout
    );
}
