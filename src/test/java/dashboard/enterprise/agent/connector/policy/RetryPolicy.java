package dashboard.enterprise.agent.connector.policy;
public record RetryPolicy(int maxAttempts, long backoffMillis) {
    public RetryPolicy { if(maxAttempts<1) throw new IllegalArgumentException("At least one attempt is required."); if(backoffMillis<0) throw new IllegalArgumentException("Backoff cannot be negative."); }
    public static RetryPolicy standard(){return new RetryPolicy(3,250);}
}
