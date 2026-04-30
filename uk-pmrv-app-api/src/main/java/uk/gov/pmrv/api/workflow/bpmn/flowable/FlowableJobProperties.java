package uk.gov.pmrv.api.workflow.bpmn.flowable;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "flowable-job")
@Data
class FlowableJobProperties {
    private int corePoolSize = 4;
    private int maxPoolSize = 4;
    private int queueCapacity = 100;
}
