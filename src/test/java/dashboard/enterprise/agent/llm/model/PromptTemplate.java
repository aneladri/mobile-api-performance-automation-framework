package dashboard.enterprise.agent.llm.model;
import java.util.Map;
public record PromptTemplate(String schemaVersion,String promptId,String version,String systemPrompt,String userTemplate,Map<String,String> metadata){
 public PromptTemplate{schemaVersion=req(schemaVersion,"Schema version");promptId=req(promptId,"Prompt id");version=req(version,"Version");systemPrompt=safe(systemPrompt);userTemplate=req(userTemplate,"User template");metadata=metadata==null?Map.of():Map.copyOf(metadata);}private static String safe(String v){return v==null?"":v.trim();}private static String req(String v,String l){String n=safe(v);if(n.isEmpty())throw new IllegalArgumentException(l+" is required.");return n;}}
