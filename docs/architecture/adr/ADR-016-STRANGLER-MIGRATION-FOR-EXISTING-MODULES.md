# ADR-016: Migrate existing modules through adapters

**Status: Accepted**

## Decision
Adopt a strangler migration. Existing Mobile, Web, API and Performance demos remain operational while capability adapters are introduced incrementally.

## Consequences
The product avoids a high-risk rewrite and preserves release confidence, at the cost of a temporary dual-path architecture.
