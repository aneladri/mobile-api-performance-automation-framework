package dashboard.enterprise.doctor.executive.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.executive.model.DoctorExecutiveOverview;
import dashboard.enterprise.doctor.executive.report.DoctorExecutiveHtmlWriter;
import dashboard.enterprise.doctor.model.DoctorSnapshot;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DoctorExecutivePublisher {

    private static final ObjectMapper MAPPER =
            JsonMapper.getInstance();

    private DoctorExecutivePublisher() {
    }

    public static void publish(
            DoctorSnapshot snapshot,
            Path outputDirectory
    ) throws Exception {

        Files.createDirectories(outputDirectory);

        DoctorExecutiveOverview overview =
                new DoctorExecutiveMapper().map(snapshot);

        Path json = outputDirectory.resolve(
                "doctor-overview.json"
        );

        Path html = outputDirectory.resolve(
                "doctor-overview.html"
        );

        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(json.toFile(), overview);

        Files.writeString(
                html,
                new DoctorExecutiveHtmlWriter()
                        .render(overview),
                StandardCharsets.UTF_8
        );

        System.out.println(
                "MAPAF Doctor Executive Overview generated:"
        );

        System.out.println("  " + html);
        System.out.println("  " + json);
    }
}
