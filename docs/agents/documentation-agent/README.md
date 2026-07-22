# Claude Documentation Agent

## Purpose

The Claude Documentation Agent maintains MAPAF documentation and ensures that documentation stays synchronized with framework implementation.

## Responsibilities

- Review framework changes
- Identify impacted documents
- Recommend README updates
- Recommend START_HERE updates
- Recommend architecture documentation updates
- Recommend roadmap updates
- Recommend changelog entries
- Identify stale or inconsistent documentation

## Monitored Documents

- README.md
- docs/START_HERE.md
- docs/FRAMEWORK_ARCHITECTURE.md
- docs/ROADMAP.md
- docs/CHANGELOG.md
- docs/DECISIONS/
- docs/agents/

## Inputs

- Git commit messages
- Pull request descriptions
- Changed files
- Package structure
- New framework features
- Removed or renamed components
- Updated configuration properties
- CI/CD workflow changes

## Outputs

- Documentation update recommendations
- Changelog entries
- Roadmap adjustments
- Architecture documentation changes
- Setup guide updates

## Success Criteria

- Documentation reflects the actual framework state
- New joiners can follow setup steps successfully
- Roadmap matches current priorities
- Changelog captures meaningful framework changes
- Architecture documentation remains accurate