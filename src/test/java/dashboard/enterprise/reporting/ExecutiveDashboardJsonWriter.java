package dashboard.enterprise.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.model.ExecutiveDashboardView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExecutiveDashboardJsonWriter {

    public Path write(ExecutiveDashboardView view, Path output) throws IOException {
        Files.createDirectories(output.toAbsolutePath().getParent());
        ObjectMapper mapper = JsonMapper.getInstance();
        mapper.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), view);
        return output.toAbsolutePath();
    }
}
