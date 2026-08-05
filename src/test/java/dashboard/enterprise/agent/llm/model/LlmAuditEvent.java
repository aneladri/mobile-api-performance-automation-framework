package dashboard.enterprise.agent.llm.model;
import java.util.Map;
public record LlmAuditEvent(String schemaVersion,String generatedAt,String correlationId,String requestId,String providerId,String modelId,String capabilityId,String status,long latencyMillis,LlmUsage usage,Map<String,String> metadata){
 public LlmAuditEvent{schemaVersion=safe(schemaVersion);generatedAt=safe(generatedAt);correlationId=safe(correlationId);requestId=safe(requestId);providerId=safe(providerId);modelId=safe(modelId);capabilityId=safe(capabilityId);status=safe(status);usage=usage==null?new LlmUsage("mapaf.llm.usage/v1",0,0,0,0):usage;metadata=metadata==null?Map.of():Map.copyOf(metadata);}private static String safe(String v){return v==null?"":v.trim();}}
