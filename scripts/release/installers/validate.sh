#!/usr/bin/env bash
set -euo pipefail
target="${1:-$PWD}"
cd "$target"
./gradlew compileTestJava
./gradlew doctorCliGate
./gradlew releaseReadinessDemo
./gradlew mapafDoctor
./gradlew mapafDoctorExecutiveReport
bash scripts/release/validate_v2_9_ga.sh "$target"
