package dashboard.enterprise.agent.llm.model;
import java.util.List;import java.util.Map;
public record LlmResponse(String schemaVersion,String requestId,String providerId,String modelId,boolean successful,String summary,String content,Map<String,Object> structuredOutput,LlmUsage usage,List<String> evidence){
 public LlmResponse{schemaVersion=safe(schemaVersion);requestId=safe(requestId);providerId=safe(providerId);modelId=safe(modelId);summary=safe(summary);content=content==null?"":content;structuredOutput=structuredOutput==null?Map.of():Map.copyOf(structuredOutput);usage=usage==null?new LlmUsage("mapaf.llm.usage/v1",0,0,0,0):usage;evidence=evidence==null?List.of():List.copyOf(evidence);}private static String safe(String v){return v==null?"":v.trim();}}
