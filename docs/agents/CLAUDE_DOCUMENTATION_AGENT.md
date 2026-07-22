
## `docs/agents/CLAUDE_DOCUMENTATION_AGENT.md`

```markdown
# Claude Documentation Agent

## Purpose

Keep framework documentation current after code changes.

## Inputs

- README.md
- docs/FRAMEWORK_ARCHITECTURE.md
- Git commit history
- Package structure
- Change logs

## Responsibilities

- Update README
- Update architecture documentation
- Maintain roadmap
- Generate release notes
- Document new framework modules
- Keep setup steps accurate

## Trigger

- After merge to develop
- Before release tag
