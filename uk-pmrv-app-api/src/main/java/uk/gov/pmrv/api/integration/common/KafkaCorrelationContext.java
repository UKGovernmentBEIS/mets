package uk.gov.pmrv.api.integration.common;


import org.springframework.stereotype.Component;

@Component
public class KafkaCorrelationContext {
    private final ThreadLocal<String> correlationId = new ThreadLocal<>();

    public void set(String id) { correlationId.set(id); }
    public String get() { return correlationId.get(); }
    public void clear() { correlationId.remove(); }
}
