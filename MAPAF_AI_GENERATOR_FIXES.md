# MAPAF AI Generator Fixes

This framework copy contains the Demo 1 code-generation fixes.

## Updated behavior

- Claude code-generation output budget increased from 1024 to 8192 tokens.
- `generateAutomation` now defaults to 8192 tokens and a 120-second timeout.
- Claude `stop_reason` and token usage are logged.
- A response ending with `stop_reason=max_tokens` fails clearly instead of writing partial Java.
- AI output is parsed once rather than twice.
- Section parsing now recognizes only exact section-marker lines.
- Markdown fences and headings are removed from generated Java.
- Multiple Screen Object classes are written as separate Java files.
- Generated Java is rejected when braces, strings, characters, or block comments are incomplete.
- The prompt enforces the exact requested target package and unresolved locator handling.

## Run

```bash
rm -rf generated/ai/automation

./gradlew generateAutomation \
  -Pstory="$(cat room-scan-story.md)" \
  -Pplatform=android \
  -PscreenName=RoomScanHomeScreen \
  -PacceptanceCriteria="User can start a room scan; Capture screen opens; Completed scan can be submitted" \
  -PtargetPackage=roomscan.generated
```

Optional explicit token override:

```bash
./gradlew generateAutomation \
  -PclaudeMaxTokens=12000 \
  -Pstory="$(cat room-scan-story.md)" \
  -Pplatform=android \
  -PscreenName=RoomScanHomeScreen \
  -PtargetPackage=roomscan.generated
```

## Validate

```bash
find generated/ai/automation -type f

grep -R -n '^package ' generated/ai/automation --include='*.java'

grep -R -n -E '```|^##|TODO_|TODO|FIXME|UnsupportedOperationException' \
  generated/ai/automation --include='*.java' \
  || echo "Generated Java quality checks passed"
```

## Verification note

The isolated modified Java classes compiled successfully with `javac` in the delivery environment.
The full Gradle build could not run there because the environment could not download the Gradle 9.5.1 distribution from `services.gradle.org`. Run `./gradlew clean compileTestJava` locally after extraction.
