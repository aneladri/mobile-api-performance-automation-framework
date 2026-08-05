package dashboard.enterprise.doctor.probe.browser;

import java.nio.file.Path;

public interface BrowserRuntime {

    BrowserRuntimeResult inspect(
            Path repositoryRoot,
            Path evidenceDirectory
    );
}
