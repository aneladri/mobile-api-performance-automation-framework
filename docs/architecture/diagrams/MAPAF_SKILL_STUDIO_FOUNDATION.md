# MAPAF Skill Studio Foundation

```text
Skill Package
  ├─ SKILL.md
  ├─ skill.json
  ├─ prompts
  ├─ examples
  ├─ policies
  └─ tests
       ↓
Loader → Validator → Evaluator → Publisher → Versioned Registry
```

Skills declare capabilities instead of provider-specific tools. Publication requires structural validation and a passing deterministic evaluation.
