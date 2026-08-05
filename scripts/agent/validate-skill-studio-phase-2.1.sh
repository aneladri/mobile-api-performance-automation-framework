#!/usr/bin/env bash
set -euo pipefail
required=(
  "src/test/java/dashboard/enterprise/agent/skillstudio/model/SkillManifest.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/model/SkillPackage.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/model/SkillTestCase.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/model/SkillEvaluationResult.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/registry/InMemorySkillPackageRegistry.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/loader/FileSystemSkillLoader.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/validation/SkillPackageValidator.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/evaluation/DeterministicSkillEvaluator.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/publisher/SkillPublisher.java"
  "src/test/java/dashboard/enterprise/agent/skillstudio/tests/SkillStudioFoundationTest.java"
  "src/test/resources/skill-studio/test-design/SKILL.md"
  "src/test/resources/skill-studio/test-design/skill.json"
  "testng-mapaf-skill-studio.xml"
  "docs/architecture/diagrams/MAPAF_SKILL_STUDIO_FOUNDATION.md"
  "docs/releases/MAPAF-v3.3-Phase-2.1-Release-Notes.md"
)
failures=0
for file in "${required[@]}"; do
  if [ -f "$file" ]; then echo "PASS: $file"; else echo "FAIL: $file"; failures=$((failures+1)); fi
done
for contract in \
  'mapaf.skill.package/v1' \
  'mapaf.skill.manifest/v1' \
  'mapaf.skill.test/v1' \
  'mapaf.skill.evaluation/v1'
do
  if grep -R -q "$contract" src/test/java/dashboard/enterprise/agent/skillstudio src/test/resources/skill-studio; then
    echo "PASS: $contract"
  else
    echo "FAIL: $contract"
    failures=$((failures+1))
  fi
done
if [ "$failures" -ne 0 ]; then echo "MAPAF v3.3 Phase 2.1 validation failed."; exit 1; fi
echo "MAPAF v3.3 Phase 2.1 validation passed."
