#!/usr/bin/env bash
set -euo pipefail
required=(updr-demo/server.js updr-demo/public/api-monitor.html updr-demo/public/api-monitor.js updr-demo/public/api-monitor.css scripts/updr/run-live-api-demo.js scripts/updr/open-api-monitor.sh scripts/updr/run-api-presentation-demo.sh)
fail=0;for f in "${required[@]}";do if [ -f "$f" ];then echo "PASS: $f";else echo "FAIL: $f";fail=$((fail+1));fi;done
for token in 'mapaf.updr.api.transaction/v1' '/api/demo/transaction/stream' 'UPDR API Transaction Monitor' 'OFFLINE_PENDING_SYNC' 'APPROVED';do if grep -R -q "$token" updr-demo scripts/updr;then echo "PASS: $token";else echo "FAIL: $token";fail=$((fail+1));fi;done
[ "$fail" -eq 0 ] || { echo 'Live API Transaction Monitor validation failed.'; exit 1; };echo 'MAPAF UPDR Live API Transaction Monitor validation passed.'
