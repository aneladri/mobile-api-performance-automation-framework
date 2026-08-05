package dashboard.enterprise.agent.llm.model;
public record LlmUsage(String schemaVersion,int promptTokens,int completionTokens,int totalTokens,double estimatedCostUsd){
 public LlmUsage{schemaVersion=schemaVersion==null?"":schemaVersion.trim();if(promptTokens<0||completionTokens<0||totalTokens<0||estimatedCostUsd<0)throw new IllegalArgumentException("Usage values cannot be negative.");if(totalTokens!=promptTokens+completionTokens)throw new IllegalArgumentException("Total tokens must equal prompt plus completion tokens.");}}
