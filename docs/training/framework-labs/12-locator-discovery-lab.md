# Lab 12 — Verified Locator Discovery and AI Code Generation

## Goal

Understand how MAPAF reduces manual locator updates after AI code generation by grounding generated code in a real Appium hierarchy.

## Design overview

```text
Real application hierarchy
          ↓
Candidate extraction
          ↓
Platform-specific ranking
          ↓
Uniqueness validation
          ↓
Verified locator context
          ↓
AI code generation
          ↓
Compile and smoke test
```

## Prerequisites

```bash
./gradlew clean compileTestJava
```

Use the included hierarchy:

```text
window_dump_views.xml
```

## Step-by-step exercise

Run:

```bash
./gradlew generateAutomation \
  -Pstory="As a user I can open Views" \
  -Pplatform=android \
  -PscreenName=ViewsScreen \
  -PtargetPackage=mobile.generated \
  -PhierarchyFile=window_dump_views.xml
```

Review:

- Generated screen/page asset
- Locator context
- Unresolved elements
- Generated metadata

## Validation checkpoint

The trainee must explain:

- Why AI must not invent locators.
- The priority of accessibility ID, resource ID, UIAutomator, and XPath.
- Why uniqueness validation matters.
- When a locator should remain unresolved.

## Extension guide

1. Capture a new Appium hierarchy.
2. Add parsing support for additional attributes.
3. Add live uniqueness validation.
4. Add a locator manifest.
5. Compile generated assets.
6. Run a smoke test before accepting generated code.

## Troubleshooting

If generated code still contains placeholders:

- Confirm the hierarchy path exists.
- Confirm the element has stable attributes.
- Confirm the parser recognized the platform.
- Do not replace placeholders with guessed XPath values.

## Best practices

- Ask developers to add accessibility identifiers.
- Keep discovery separate from runtime healing.
- Persist approved healed locators.
- Require review until generation confidence is proven.
