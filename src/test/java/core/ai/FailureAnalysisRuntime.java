package core.ai;

public class FailureAnalysisRuntime {

    public AgentResponse analyze(String log) {

        AgentResponse response =
                new AgentResponse();

        if (log.contains("SSLHandshakeException")) {

            response.setClassification(
                    "SSL Configuration Failure"
            );

            response.setRootCause(
                    "Certificate not trusted"
            );

            response.setRecommendation(
                    "Import certificates into Java trust store"
            );

            response.setConfidence(95);
        }

        return response;
    }
}