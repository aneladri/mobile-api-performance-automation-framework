# Lab 07b – AI Locator Healing Exercise

## Goal

By the end of this lab, you will understand:

* How MAPAF locator healing works
* How to trigger healing intentionally
* How healing reports are generated
* How to interpret healing recommendations
* How to promote healed locators into Screen Objects

---

# Why Does Locator Healing Exist?

Mobile applications change frequently.

Common changes:

```text id="changes"
Resource ID renamed

Accessibility ID changed

Visible text changed

Element moved
```

Traditional automation:

```text id="traditional"
Locator breaks
↓
Test fails
```

MAPAF:

```text id="mapaf"
Locator breaks
↓
Healing Engine
↓
Recommendation
↓
Report
```

---

# Healing Architecture

```text id="flow"
Broken Locator
↓
HealedLocatorStore

↓ miss

LocalHealingRuleEngine

↓ miss

HealingBudgetGuard

↓ allowed

Claude API (future)
```

---

# Healing Tiers

## Tier 1

Cache

Purpose:

Reuse previously healed locators.

Cost:

```text id="tier1"
$0.00
```

---

## Tier 2

Local Rule Engine

Rules:

```text id="rules"
ID Variants

Text XPath

Accessibility Scan

Class + Index
```

Cost:

```text id="tier2"
$0.00
```

---

## Tier 3

Claude Healing

Purpose:

AI locator recommendation.

Cost:

```text id="tier3"
Paid
```

Protected by:

```text id="guard"
HealingBudgetGuard
```

---

# Exercise Setup

Open:

```text id="screen"
src/test/java/mobile/screens/LoginScreen.java
```

Locate:

```java id="locator"
private final By loginButton =
        By.id("login_button");
```

Intentionally break it:

```java id="broken"
private final By loginButton =
        By.id("login_button_broken");
```

---

# Execute Test

Run:

```bash id="run"
gradle mobileTest
```

Expected:

```text id="expected"
Locator Failure
```

---

# Observe Healing Logs

Look for:

```text id="logs"
[Healing]

Tier 1

Tier 2

Budget
```

Example:

```text id="example"
[Healing] Starting tiered healing
[Healing] Tier 2 HIT - local rule
```

---

# Review Healing Report

Open:

```bash id="report"
ls reports/ai/generated
```

Expected:

```text id="generated"
healing-metrics-report.md
```

Review:

```bash id="open"
cat reports/ai/generated/healing-metrics-report.md
```

---

# Interpret Results

Questions:

```text id="questions"
Was the locator healed?

Which rule succeeded?

Was Claude required?

What confidence was reported?
```

---

# Promote Healed Locator

If healing identifies:

```text id="newlocator"
accessibilityId=loginButton
```

Update the Screen Object:

```java id="fix"
private final By loginButton =
        AppiumBy.accessibilityId(
                "loginButton"
        );
```

---

# Re-run Test

Execute:

```bash id="rerun"
gradle mobileTest
```

Expected:

```text id="pass"
BUILD SUCCESSFUL
```

---

# Engineering Principle

Healing is:

```text id="principle"
Temporary Recovery
```

It is NOT:

```text id="wrong"
Permanent Fix
```

The engineer must always:

```text id="correct"
Review recommendation
↓
Update Screen Object
↓
Commit fix
```

---

# Common Mistakes

## Mistake

Ignoring healing reports.

Result:

Repeated healing events.

---

## Mistake

Leaving broken locators in source code.

Result:

Dependence on healing.

---

## Mistake

Assuming healing is always correct.

Result:

False positives.

---

# Checkpoint

The trainee should be able to answer:

1. Why does locator healing exist?
2. What are the three healing tiers?
3. What does HealingBudgetGuard do?
4. What is the purpose of HealedLocatorStore?
5. Why should healed locators be promoted into Screen Objects?
6. Why is healing considered a temporary recovery mechanism?
7. What is the difference between Tier 2 and Tier 3 healing?

