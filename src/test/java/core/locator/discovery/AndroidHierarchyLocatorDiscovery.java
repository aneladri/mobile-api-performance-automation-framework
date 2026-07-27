package core.locator.discovery;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AndroidHierarchyLocatorDiscovery {

    public List<LocatorCandidate> discover(Path hierarchyFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            Document document = factory.newDocumentBuilder().parse(hierarchyFile.toFile());
            NodeList nodes = document.getElementsByTagName("node");

            Map<String, Integer> accessibilityCounts = new HashMap<>();
            Map<String, Integer> resourceIdCounts = new HashMap<>();
            Map<String, Integer> textCounts = new HashMap<>();

            for (int index = 0; index < nodes.getLength(); index++) {
                Element element = (Element) nodes.item(index);
                increment(accessibilityCounts, attribute(element, "content-desc"));
                increment(resourceIdCounts, attribute(element, "resource-id"));
                increment(textCounts, attribute(element, "text"));
            }

            List<LocatorCandidate> candidates = new ArrayList<>();
            for (int index = 0; index < nodes.getLength(); index++) {
                Element element = (Element) nodes.item(index);
                String contentDescription = attribute(element, "content-desc");
                String resourceId = attribute(element, "resource-id");
                String text = attribute(element, "text");
                String elementClass = attribute(element, "class");

                if (!contentDescription.isBlank()) {
                    candidates.add(candidate(
                            contentDescription,
                            elementClass,
                            "ACCESSIBILITY_ID",
                            contentDescription,
                            accessibilityCounts.getOrDefault(contentDescription, 0) == 1,
                            95,
                            hierarchyFile
                    ));
                    continue;
                }

                if (!resourceId.isBlank()) {
                    candidates.add(candidate(
                            resourceId.substring(resourceId.lastIndexOf('/') + 1),
                            elementClass,
                            "RESOURCE_ID",
                            resourceId,
                            resourceIdCounts.getOrDefault(resourceId, 0) == 1,
                            88,
                            hierarchyFile
                    ));
                    continue;
                }

                if (!text.isBlank()) {
                    candidates.add(candidate(
                            text,
                            elementClass,
                            "ANDROID_UIAUTOMATOR",
                            "new UiSelector().text(\"" + escape(text) + "\")",
                            textCounts.getOrDefault(text, 0) == 1,
                            72,
                            hierarchyFile
                    ));
                }
            }

            return candidates.stream()
                    .filter(LocatorCandidate::unique)
                    .toList();
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Unable to discover Android locators from " + hierarchyFile,
                    exception
            );
        }
    }

    private LocatorCandidate candidate(
            String label,
            String elementClass,
            String type,
            String value,
            boolean unique,
            int baseConfidence,
            Path source
    ) {
        int confidence = unique ? baseConfidence : Math.max(40, baseConfidence - 30);
        return new LocatorCandidate(
                toLogicalName(label),
                elementClass,
                type,
                value,
                confidence,
                unique,
                source.toString()
        );
    }

    private static String attribute(Element element, String name) {
        return element.hasAttribute(name) ? element.getAttribute(name).trim() : "";
    }

    private static void increment(Map<String, Integer> counts, String value) {
        if (!value.isBlank()) {
            counts.merge(value, 1, Integer::sum);
        }
    }

    private static String toLogicalName(String value) {
        String cleaned = value
                .replaceAll("[^A-Za-z0-9]+", " ")
                .trim();
        if (cleaned.isBlank()) {
            return "element";
        }
        String[] words = cleaned.split("\\s+");
        StringBuilder result = new StringBuilder(words[0].toLowerCase(Locale.ROOT));
        for (int index = 1; index < words.length; index++) {
            String word = words[index];
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase(Locale.ROOT));
        }
        return result.toString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
