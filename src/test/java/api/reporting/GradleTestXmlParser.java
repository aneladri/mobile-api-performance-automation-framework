package api.reporting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class GradleTestXmlParser {

    public ApiTestResultParser parseDirectory(Path directory)
            throws Exception {

        ApiTestResultParser result = new ApiTestResultParser();

        if (directory == null || !Files.isDirectory(directory)) {
            return result;
        }

        try (Stream<Path> files = Files.walk(directory)) {
            List<Path> xmlFiles = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName()
                            .toString()
                            .toLowerCase(Locale.ROOT)
                            .endsWith(".xml"))
                    .toList();

            for (Path file : xmlFiles) {
                parseFile(file, result);
            }
        }

        return result;
    }

    private void parseFile(
            Path file,
            ApiTestResultParser result
    ) throws Exception {

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl",
                true
        );

        factory.setFeature(
                "http://xml.org/sax/features/external-general-entities",
                false
        );

        factory.setFeature(
                "http://xml.org/sax/features/external-parameter-entities",
                false
        );

        factory.setAttribute(
                XMLConstants.ACCESS_EXTERNAL_DTD,
                ""
        );

        factory.setAttribute(
                XMLConstants.ACCESS_EXTERNAL_SCHEMA,
                ""
        );

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file.toFile());

        Element root = document.getDocumentElement();

        if (root == null || !"testsuite".equals(root.getTagName())) {
            return;
        }

        int total = parseInt(root.getAttribute("tests"));
        int failed = parseInt(root.getAttribute("failures"))
                + parseInt(root.getAttribute("errors"));
        int skipped = parseInt(root.getAttribute("skipped"));
        int passed = Math.max(0, total - failed - skipped);

        long durationMilliseconds = Math.round(
                parseDouble(root.getAttribute("time")) * 1000.0
        );

        result.add(
                total,
                passed,
                failed,
                skipped,
                durationMilliseconds
        );
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return 0.0;
        }
    }
}
