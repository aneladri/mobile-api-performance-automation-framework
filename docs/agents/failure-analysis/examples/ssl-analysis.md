# SSL Certificate Failure Analysis

## Input Failure

```text
javax.net.ssl.SSLHandshakeException

PKIX path building failed

sun.security.provider.certpath.SunCertPathBuilderException:
unable to find valid certification path to requested target
```

---

## Failure Type

SSL / Certificate Failure

---

## Root Cause

Java was unable to validate the server certificate because the certificate chain was not trusted by the local Java truststore.

---

## Evidence

Exception:

```text
SSLHandshakeException
```

Underlying Cause:

```text
PKIX path building failed
```

Certificate Validation Error:

```text
unable to find valid certification path to requested target
```

---

## Suggested Fix

### Immediate Fix

Import the required certificates into the Java truststore.

macOS:

```bash
security find-certificate -a -p \
/System/Library/Keychains/SystemRootCertificates.keychain \
> /tmp/macos-certs.pem
```

Then:

```bash
sudo keytool -importcert \
-trustcacerts \
-noprompt \
-alias macos-root-certs \
-file /tmp/macos-certs.pem \
-keystore "$JAVA_HOME/lib/security/cacerts" \
-storepass changeit
```

---

### Verification

```bash
gradle clean apiTest
```

Expected:

```text
BUILD SUCCESSFUL
```

---

### Preventive Action

Document certificate setup in:

```text
docs/START_HERE.md
```

---

## Confidence Score

99%

---

## Recommended Owner

DevOps / Automation Engineer

---

## Recommended Next Action

Validate truststore configuration and rerun API tests.

