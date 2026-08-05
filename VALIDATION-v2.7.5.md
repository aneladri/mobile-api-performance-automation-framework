# MAPAF v2.7.5 Validation

## Automated checks

```bash
./gradlew clean compileTestJava
./gradlew test --tests core.enterprise.tests.BusinessTransactionTest
```

## Business transaction verification

```bash
for file in \
  mobile/reports/enterprise-summary.json \
  web/reports/enterprise-summary.json \
  api/reports/enterprise-summary.json
do
  echo "===== $file ====="
  python3 - <<PY
import json
with open("$file", encoding="utf-8") as stream:
    data = json.load(stream)
print("Execution ID:", data.get("executionId"))
print("Transactions:", len(data.get("businessTransactions", [])))
for transaction in data.get("businessTransactions", []):
    print("-", transaction.get("name"), transaction.get("status"), transaction.get("p95Ms"), "ms")
PY
done
```

Expected counts: Mobile 7, Web 4, API 5.
