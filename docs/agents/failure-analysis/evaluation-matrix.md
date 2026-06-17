# Failure Analysis Evaluation Matrix

| Sample Failure                   | Expected Classification      | Expected Confidence |
| -------------------------------- | ---------------------------- | ------------------- |
| android-home-missing.log         | Environment Configuration    | 98%                 |
| browserstack-session-failure.log | BrowserStack Session Failure | 95%                 |
| ssl-handshake-failure.log        | SSL / Certificate Failure    | 99%                 |

## Evaluation Rules

The Claude Failure Analysis Agent should:

1. Correctly classify the failure.
2. Identify the root cause.
3. Provide a suggested fix.
4. Assign an appropriate confidence score.
5. Recommend the correct owner.

## Pass Criteria

* Classification accuracy ≥ 90%
* Root cause accuracy ≥ 85%
* Suggested fix relevance ≥ 85%
* Confidence score within ±10% of expected

