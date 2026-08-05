package core.ai.generation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceWriter implements SourceCodeWriter {

    private static final Pattern PUBLIC_TYPE_PATTERN = Pattern.compile(
            "\\bpublic\\s+(?:final\\s+|abstract\\s+)?"
                    + "(?:class|interface|enum|record)\\s+"
                    + "([A-Za-z_$][A-Za-z\\d_$]*)"
    );

    private static final Pattern PACKAGE_BOUNDARY =
            Pattern.compile("(?m)(?=^\\s*package\\s+[\\w.]+\\s*;)");

    @Override
    public void write(AutomationProject project, String outputDirectory) {
        try {
            Path outputPath = Path.of(outputDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(outputPath);

            writeJavaSources(outputPath, project.getScreenObject(), "screen object");
            writeJavaSources(outputPath, project.getBusinessFlow(), "business flow");
            writeJavaSources(outputPath, project.getTestClass(), "test class");

            writeTextFile(outputPath.resolve("ASSERTIONS.md"), project.getAssertions());
            writeTextFile(outputPath.resolve("TEST_DATA.md"), project.getTestData());
            writeTextFile(outputPath.resolve("TODO_ITEMS.md"), project.getTodoItems());

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to write generated automation project",
                    exception
            );
        }
    }

    private void writeJavaSources(
            Path outputPath,
            String section,
            String sectionName
    ) throws IOException {
        if (section == null || section.isBlank()) {
            return;
        }

        List<String> sources = splitJavaSources(section);
        if (sources.isEmpty()) {
            throw new IllegalArgumentException(
                    "No Java source was found in generated " + sectionName
            );
        }

        for (String source : sources) {
            String cleanedSource = cleanJavaSource(source);
            String className = extractPublicTypeName(cleanedSource);
            validateJavaSource(cleanedSource, className, sectionName);

            Path destination = outputPath.resolve(className + ".java").normalize();
            if (!destination.startsWith(outputPath)) {
                throw new IllegalArgumentException(
                        "Generated source attempted to write outside output directory"
                );
            }

            Files.writeString(destination, cleanedSource);
        }
    }

    private List<String> splitJavaSources(String content) {
        String cleaned = cleanJavaSource(content).trim();
        String[] candidates = PACKAGE_BOUNDARY.split(cleaned);
        List<String> sources = new ArrayList<>();

        for (String candidate : candidates) {
            String source = candidate.trim();
            if (!source.isBlank() && PUBLIC_TYPE_PATTERN.matcher(source).find()) {
                sources.add(source);
            }
        }

        if (sources.isEmpty() && PUBLIC_TYPE_PATTERN.matcher(cleaned).find()) {
            sources.add(cleaned);
        }

        return sources;
    }

    private String extractPublicTypeName(String source) {
        Matcher matcher = PUBLIC_TYPE_PATTERN.matcher(source);
        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Generated Java source does not contain a public type"
            );
        }
        return matcher.group(1);
    }

    private void validateJavaSource(
            String source,
            String className,
            String sectionName
    ) {
        if (source.contains("```") || source.matches("(?s).*^\\s*#{1,6}\\s+.*$.*")) {
            throw new IllegalArgumentException(
                    "Markdown detected in generated " + sectionName
            );
        }

        if (!source.matches("(?s).*\\bpackage\\s+[\\w.]+\\s*;.*")) {
            throw new IllegalArgumentException(
                    "Package declaration is missing from generated " + className
            );
        }

        Matcher matcher = PUBLIC_TYPE_PATTERN.matcher(source);
        int publicTypeCount = 0;
        while (matcher.find()) {
            publicTypeCount++;
        }

        if (publicTypeCount != 1) {
            throw new IllegalArgumentException(
                    "Generated file " + className + ".java contains "
                            + publicTypeCount + " public types"
            );
        }

        validateBalancedBraces(source, className);
    }

    private void validateBalancedBraces(String source, String className) {
        int balance = 0;
        boolean inString = false;
        boolean inCharacter = false;
        boolean escaped = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int index = 0; index < source.length(); index++) {
            char current = source.charAt(index);
            char next = index + 1 < source.length() ? source.charAt(index + 1) : '\0';

            if (inLineComment) {
                if (current == '\n') {
                    inLineComment = false;
                }
                continue;
            }

            if (inBlockComment) {
                if (current == '*' && next == '/') {
                    inBlockComment = false;
                    index++;
                }
                continue;
            }

            if (!inString && !inCharacter && current == '/' && next == '/') {
                inLineComment = true;
                index++;
                continue;
            }

            if (!inString && !inCharacter && current == '/' && next == '*') {
                inBlockComment = true;
                index++;
                continue;
            }

            if (escaped) {
                escaped = false;
                continue;
            }

            if ((inString || inCharacter) && current == '\\') {
                escaped = true;
                continue;
            }

            if (!inCharacter && current == '"') {
                inString = !inString;
                continue;
            }

            if (!inString && current == '\'') {
                inCharacter = !inCharacter;
                continue;
            }

            if (!inString && !inCharacter) {
                if (current == '{') {
                    balance++;
                } else if (current == '}') {
                    balance--;
                    if (balance < 0) {
                        break;
                    }
                }
            }
        }

        if (balance != 0 || inString || inCharacter || inBlockComment) {
            throw new IllegalArgumentException(
                    "Generated Java source appears truncated or incomplete: "
                            + className
            );
        }
    }

    private String cleanJavaSource(String source) {
        return (source == null ? "" : source)
                .replaceAll("(?m)^\\s*```(?:java)?\\s*$", "")
                .replaceAll("(?m)^\\s*#{1,6}\\s+.*$", "")
                .trim()
                + System.lineSeparator();
    }

    private void writeTextFile(Path file, String content) throws IOException {
        Files.writeString(file, content == null ? "" : content.trim());
    }
}
