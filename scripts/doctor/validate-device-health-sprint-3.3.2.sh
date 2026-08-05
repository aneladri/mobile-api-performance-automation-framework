#!/usr/bin/env bash
set -euo pipefail

required=(
  "src/test/java/dashboard/enterprise/doctor/probe/device/DeviceInventoryResult.java"
  "src/test/java/dashboard/enterprise/doctor/probe/device/DeviceInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/device/SystemDeviceInspector.java"
  "src/test/java/dashboard/enterprise/doctor/probe/device/DeviceHealthProbe.java"
  "src/test/java/dashboard/enterprise/doctor/tests/DeviceHealthProbeTest.java"
  "docs/releases/MAPAF-v2.9-Sprint-3.3.2-Release-Notes.md"
)

failures=0

for file in "${required[@]}"; do
  if [ -f "$file" ]; then
    echo "PASS: $file"
  else
    echo "FAIL: $file"
    failures=$((failures + 1))
  fi
done

publisher="src/test/java/dashboard/enterprise/doctor/publisher/MapafDoctorPublisher.java"

if grep -q 'new DeviceHealthProbe()' "$publisher"; then
  echo "PASS: Device Health Probe registered"
else
  echo "FAIL: Device Health Probe not registered"
  failures=$((failures + 1))
fi

probe="src/test/java/dashboard/enterprise/doctor/probe/device/DeviceHealthProbe.java"

for mode in \
  "ANDROID_REQUIRED" \
  "IOS_REQUIRED" \
  "ANY_DEVICE_REQUIRED"
do
  if grep -q "$mode" "$probe"; then
    echo "PASS: device execution mode $mode"
  else
    echo "FAIL: device execution mode missing $mode"
    failures=$((failures + 1))
  fi
done

if [ "$failures" -ne 0 ]; then
  echo "Device Health Sprint 3.3.2 validation failed."
  exit 1
fi

echo "MAPAF v2.9 Sprint 3.3.2 validation passed."
