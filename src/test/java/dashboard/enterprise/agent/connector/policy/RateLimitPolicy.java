package dashboard.enterprise.agent.connector.policy;
public record RateLimitPolicy(int requestsPerMinute) {
    public RateLimitPolicy { if(requestsPerMinute<1) throw new IllegalArgumentException("Rate limit must be positive."); }
    public static RateLimitPolicy standard(){return new RateLimitPolicy(60);}
}
