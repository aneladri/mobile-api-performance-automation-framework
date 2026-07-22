# Locator Discovery and Verified Code Generation

## Objective

Generate automation code with locators grounded in a real Android UI hierarchy instead of placeholder values.

## Initial Android flow

1. Capture Appium page source for the target screen.
2. Pass the XML file to the generation task.
3. The framework extracts unique locator candidates in this order:
   - Accessibility ID
   - Resource ID
   - Android UIAutomator text selector
4. Verified candidates are added to the AI prompt.
5. The generator is instructed to use only supplied values and to mark missing elements as unresolved.

## Command

```bash
./gradlew generateAutomation \
  -Pstory="As a user I can open Views" \
  -Pplatform=android \
  -PscreenName=ViewsScreen \
  -PtargetPackage=mobile.generated \
  -PhierarchyFile=window_dump.xml
```

Use `-PdryRun=true` to verify task wiring without calling the AI provider.

## Current scope

- Android XML hierarchy parsing
- Stable candidate ranking
- Uniqueness checks within the supplied hierarchy
- Prompt grounding

## Next increments

- Live Appium validation against the active driver
- Locator manifest JSON and validation report
- Playwright DOM discovery using role, label, test ID, and stable CSS attributes
- iOS hierarchy support
- Automatic update after approved self-healing
