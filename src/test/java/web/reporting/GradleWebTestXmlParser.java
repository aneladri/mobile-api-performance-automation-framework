package web.reporting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class GradleWebTestXmlParser {

    public WebTestResultParser parseDirectory(
            Path testResultsDirectory
    ) throws Exception {

        WebTestResultParser result =
                new WebTestResultParser();

        if (testResultsDirectory == null
                || !Files.isDirectory(
                        testResultsDirectory
                )) {

            return result;
        }

        try (Stream<Path> files =
                     Files.list(testResultsDirectory)) {

            for (Path file : files
                    .filter(Files::isRegularFile)
                    .filter(this::isTestXmlFile)
                    .toList()) {

                parseFile(file, result);
            }
        }

        return result;
    }

    private void parseFile(
            Path xmlFile,
            WebTestResultParser result
    ) throws Exception {

        Document document =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(xmlFile.toFile());

        Element root =
                document.getDocumentElement();

        if (root == null
                || !"testsuite".equals(
                        root.getTagName()
                )) {

            return;
        }

        int tests = readIntegerAttribute(
                root,
                "tests"
        );

        int failures = readIntegerAttribute(
                root,
                "failures"
        );

        int errors = readIntegerAttribute(
                root,
                "errors"
        );

        int skipped = readIntegerAttribute(
                root,
                "skipped"
        );

        double duration = readDoubleAttribute(
                root,
                "time"
        );

        int failed = failures + errors;

        int passed = Math.max(
                0,
                tests - failed - skipped
        );

        result.setTotal(
                result.getTotal() + tests
        );

        result.setPassed(
                result.getPassed() + passed
        );

        result.setFailed(
                result.getFailed() + failed
        );

        result.setSkipped(
                result.getSkipped() + skipped
        );

        result.setDurationSeconds(
                result.getDurationSeconds()
                        + duration
        );

        result.setFileCount(
                result.getFileCount() + 1
        );
    }

    private boolean isTestXmlFile(
            Path path
    ) {
        String fileName =
                path.getFileName().toString();

        return fileName.startsWith("TEST-")
                && fileName.endsWith(".xml");
    }

    private int readIntegerAttribute(
            Element element,
            String attribute
    ) {
        String value =
                element.getAttribute(attribute);

        if (value == null || value.isBlank()) {
            return 0;
        }

        return Integer.parseInt(value);
    }

    private double readDoubleAttribute(
            Element element,
            String attribute
    ) {
        String value =
                element.getAttribute(attribute);

        if (value == null || value.isBlank()) {
            return 0;
        }

        return Double.parseDouble(value);
    }
}
