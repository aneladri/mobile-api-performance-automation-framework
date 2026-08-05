#!/usr/bin/env bash
set -euo pipefail
curl -fsS -X POST http://localhost:8090/api/demo/reset
printf '\n'
