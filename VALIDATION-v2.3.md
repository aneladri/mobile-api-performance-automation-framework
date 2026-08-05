# MAPAF Enterprise v2.3 Validation

The Common Enterprise Runtime source set was syntax-validated with `javac` in the delivery environment.

Full Gradle execution could not be completed in the delivery environment because external access to `services.gradle.org` was unavailable. Run these commands locally:

```bash
./gradlew clean compileTestJava
./gradlew enterpriseRuntimeTest
./gradlew apiEnterpriseDemo
./gradlew performanceEnterpriseDemo
```
