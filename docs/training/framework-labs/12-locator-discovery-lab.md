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

---

# Bridge to Playwright and the AI Phase

The same evidence-first locator principles used for Appium apply to Playwright. The source of evidence changes from a mobile hierarchy to the browser DOM and accessibility tree.

## Playwright locator discovery flow

```text
Rendered page and accessibility tree
        |
        v
Candidate discovery
(role, label, text, test id, stable attributes)
        |
        v
Ranking and uniqueness validation
        |
        v
Verified locator manifest
        |
        v
Page object or component update
        |
        v
Compile and targeted browser smoke test
```

## Recommended Playwright locator priority

Use the most user-facing and stable locator available:

1. `getByRole()` with an accessible name
2. `getByLabel()` for labelled form controls
3. `getByPlaceholder()` when the placeholder is stable and meaningful
4. `getByText()` for unique and stable visible content
5. `getByTestId()` when the application provides an approved test identifier
6. Stable CSS attributes
7. XPath only as a controlled last resort

Example:

```java
page.getByRole(AriaRole.BUTTON,
    new Page.GetByRoleOptions().setName("Sign in"));
```

Avoid fragile selectors such as:

```text
nth-child chains
absolute XPath
randomly generated class names
position-only selectors
text that changes by environment or locale
```

## AI-assisted locator generation

An AI locator service should receive grounded evidence rather than only a natural-language element description.

Recommended input:

```text
Element intent
Current locator and failure message
Relevant DOM excerpt
Accessibility snapshot
Page URL or logical screen name
Browser name
Screenshot reference
Approved locator policy
```

Recommended output:

```json
{
  "status": "CANDIDATE_FOUND",
  "recommendedStrategy": "ROLE",
  "locator": "button[name='Sign in']",
  "playwrightExample": "page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(\"Sign in\"))",
  "uniqueMatches": 1,
  "confidence": 0.94,
  "reason": "Unique accessible button with a stable name",
  "requiresApproval": true
}
```

## Controlled auto-healing concept

```text
Primary locator fails
        |
        v
Capture DOM, accessibility data, screenshot, and error
        |
        v
Search approved fallback locator repository
        |
        +--> Valid unique fallback found --> controlled retry
        |
        `--> No approved fallback
                   |
                   v
           AI proposes candidate
                   |
                   v
        Uniqueness and policy validation
                   |
                   v
         Human approval or quarantine
```

Auto-healing must not silently replace locators in source code during normal execution. A safe implementation should distinguish between:

- Runtime fallback used for the current execution
- Proposed source-code update
- Approved and persisted locator update

## Locator healing audit record

Each healing event should capture:

| Field | Purpose |
|---|---|
| Test and page | Identifies the affected automation asset |
| Original locator | Shows what failed |
| Proposed locator | Records the candidate |
| Evidence | DOM, hierarchy, screenshot, and error context |
| Match count | Confirms uniqueness |
| Confidence | Supports review prioritisation |
| Browser/platform | Identifies compatibility scope |
| Decision | Approved, rejected, or quarantined |
| Reviewer and timestamp | Supports auditability |

## Playwright exercise

1. Choose a page containing a stable labelled control.
2. Inspect its role, accessible name, test ID, and stable attributes.
3. Create at least three locator candidates.
4. Rank them using the recommended priority.
5. Verify that the selected locator returns exactly one element.
6. Run the test in Chromium, Firefox, and WebKit.
7. Record whether the locator behaves consistently across browsers.
8. Document one fallback locator, but do not use it unless the primary locator fails.

Example verification concept:

```java
Locator signIn = page.getByRole(
    AriaRole.BUTTON,
    new Page.GetByRoleOptions().setName("Sign in")
);

if (signIn.count() != 1) {
    throw new IllegalStateException(
        "Expected one Sign in button but found " + signIn.count()
    );
}
```

## AI-phase readiness checklist

- Locator policies are documented.
- Approved locator manifests are version controlled.
- Failed locator evidence can be captured automatically.
- Candidate uniqueness can be evaluated before use.
- Proposed changes are separated from approved changes.
- Healing retries are limited and visible in reports.
- Dashboard reporting can distinguish original pass, retry pass, healed pass, and failure.

## Updated completion criteria

The trainee can:

- Explain the difference between locator discovery and auto-healing.
- Rank Playwright and Appium locator strategies correctly.
- Validate candidate uniqueness.
- Describe the evidence required for an AI-generated locator.
- Explain why AI-generated changes require policy validation and audit history.
- Describe how locator healing results should appear in the unified dashboard.
