# Documentation Agent Workflow v1

## Purpose

Define how the Claude Documentation Agent reviews framework changes and recommends documentation updates.

---

## Input

The agent receives one or more of the following:

- Git commit summary
- Pull request summary
- Changed file list
- Framework package structure
- New test suites
- New Gradle tasks
- New CI/CD workflows
- New configuration properties
- New framework features

---

## Processing Steps

### Step 1 - Identify Changed Areas

Classify changes into one or more areas:

- Core Framework
- API Automation
- Android Automation
- iOS Automation
- Performance Testing
- Reporting
- CI/CD
- BrowserStack / Cloud Execution
- AI Agent Layer
- Documentation

---

### Step 2 - Identify Impacted Documents

Map changed areas to documentation.

| Changed Area | Documents to Review |
|-------------|---------------------|
| Core Framework | README.md, FRAMEWORK_ARCHITECTURE.md |
| API Automation | README.md, START_HERE.md, ROADMAP.md |
| Android Automation | START_HERE.md, FRAMEWORK_ARCHITECTURE.md |
| iOS Automation | START_HERE.md, FRAMEWORK_ARCHITECTURE.md, ROADMAP.md |
| Performance Testing | START_HERE.md, ROADMAP.md, CHANGELOG.md |
| Reporting | README.md, START_HERE.md |
| CI/CD | README.md, START_HERE.md, FRAMEWORK_ARCHITECTURE.md |
| BrowserStack | START_HERE.md, ROADMAP.md, ADRs |
| AI Agent Layer | docs/agents, ROADMAP.md, FRAMEWORK_ARCHITECTURE.md |

---

### Step 3 - Generate Documentation Recommendations

For each impacted document, recommend:

- Section to update
- New content to add
- Content to remove
- Terminology corrections
- Missing setup steps
- Changelog entry

---

### Step 4 - Validate Consistency

Check for:

- Product name consistency: MAPAF
- Ownership consistency: Quality Engineering Team
- Version consistency
- Correct command names
- Correct Gradle tasks
- Correct file paths
- Correct workflow names

---

## Output Format

```text
Changed Area:

Impacted Documents:

Recommended Updates:

Changelog Entry:

Roadmap Impact:

Confidence Score: