# MAPAF Executive Decision Flow

```text
Capability Execution
        |
        v
Enterprise Evidence
        |
        v
Quality Gate Context
        |
        v
Seven Governed Gate Evaluators
        |
        +--> BLOCKER failures
        |
        +--> HIGH concerns
        |
        +--> ADVISORY concerns
        |
        v
Release Readiness Snapshot v3
        |
        v
Executive Decision Engine
        |
        +--> READY
        |
        +--> CONDITIONAL
        |
        +--> BLOCKED
        |
        v
Executive Decision Page and JSON Contract

## 11. Add release notes

```bash
cat > docs/releases/MAPAF-v2.8-Sprint-2.4-Release-Notes.md <<'EOF'
# MAPAF v2.8 Sprint 2.4

## Executive Decision Experience

### Delivered

- Executive Decision Engine
- READY, CONDITIONAL, and BLOCKED outcomes
- Human approval requirement
- Decision confidence
- Blocking issue summary
- Advisory concern summary
- Corrective action summary
- Release Readiness v3 contract
- Enhanced executive Release Readiness UI
- Green, conditional, and blocked demo scenarios
- Decision-engine unit tests
- Architecture documentation

### Decision outcomes

- READY FOR PRODUCTION
- CONDITIONAL RELEASE — EXECUTIVE APPROVAL REQUIRED
- DO NOT RELEASE

### Contract versions

- `mapaf.release-readiness/v3`
- `mapaf.executive-decision/v1`
