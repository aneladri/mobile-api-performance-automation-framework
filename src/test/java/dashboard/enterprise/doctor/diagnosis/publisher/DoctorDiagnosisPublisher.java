package dashboard.enterprise.doctor.diagnosis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.json.JsonMapper;
import dashboard.enterprise.doctor.diagnosis.engine.DefaultDoctorDiagnosisEngine;
import dashboard.enterprise.doctor.diagnosis.model.DiagnosisSnapshot;
import dashboard.enterprise.doctor.diagnosis.report.DoctorDiagnosisHtmlWriter;
import dashboard.enterprise.doctor.model.DoctorSnapshot;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DoctorDiagnosisPublisher {
    private static final ObjectMapper MAPPER = JsonMapper.getInstance();

    private DoctorDiagnosisPublisher() {
    }

    public static void publish(DoctorSnapshot snapshot, Path outputDirectory) throws Exception {
        Files.createDirectories(outputDirectory);
        DiagnosisSnapshot diagnosis = new DefaultDoctorDiagnosisEngine().diagnose(snapshot);
        Path json = outputDirectory.resolve("doctor-diagnosis.json");
        Path html = outputDirectory.resolve("doctor-diagnosis.html");
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(json.toFile(), diagnosis);
        Files.writeString(html, new DoctorDiagnosisHtmlWriter().render(diagnosis), StandardCharsets.UTF_8);
        System.out.println("MAPAF Doctor Diagnosis Center generated:");
        System.out.println("  " + html);
        System.out.println("  " + json);
    }
}
