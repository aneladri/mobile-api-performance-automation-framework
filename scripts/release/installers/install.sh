#!/usr/bin/env bash
set -euo pipefail
source_dir="${1:-$(pwd)/framework}"
target="${2:-$HOME/MAPAF-Enterprise-v2.9.0}"
[ -f "$source_dir/MAPAF_VERSION" ] || { echo "Framework source not found: $source_dir"; exit 1; }
[ "$(tr -d '[:space:]' < "$source_dir/MAPAF_VERSION")" = "2.9.0" ] || { echo "Unexpected version"; exit 1; }
[ ! -e "$target" ] || { echo "Target already exists: $target"; exit 1; }
cp -R "$source_dir" "$target"
cd "$target"
chmod +x gradlew mapaf-doctor 2>/dev/null || true
./gradlew compileTestJava
./mapaf-doctor report --no-refresh || ./gradlew mapafDoctorExecutiveReport
echo "MAPAF Enterprise v2.9.0 installed at $target"
