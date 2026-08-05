#!/usr/bin/env bash
set -euo pipefail
required=(
 updr-demo/server.js updr-demo/data/fixtures.js updr-demo/public/admin.html updr-demo/public/inspector.html updr-demo/public/qc.html
 src/test/java/api/updr/demo/UpdrApiDemoTest.java src/test/java/web/updr/demo/UpdrWebDemoTest.java src/test/java/web/updr/demo/UpdrMobileDemoTest.java
 src/test/resources/testng-updr-mobile-demo.xml performance/updr/k6/updr-integrated-quality.js scripts/updr/generate-updr-integrated-report.js
 docs/releases/MAPAF-v3.3-UPDR-Integrated-Quality-Demo-Release-Notes.md
)
fail=0;for f in "${required[@]}";do if [ -f "$f" ];then echo "PASS: $f";else echo "FAIL: $f";fail=$((fail+1));fi;done
for token in 'mapaf.updr.integrated.quality.summary/v1' 'OFFLINE_PENDING_SYNC' 'VALIDATION_FAILED' 'DUPLICATE_EVIDENCE_REJECTED' 'RESUBMITTED' 'APPROVED';do if grep -R -q "$token" updr-demo src/test scripts/updr;then echo "PASS: $token";else echo "FAIL: $token";fail=$((fail+1));fi;done
if [ "$fail" -ne 0 ];then echo 'MAPAF UPDR Integrated Quality validation failed.';exit 1;fi
echo 'MAPAF v3.3 UPDR Integrated Quality Demo validation passed.'
