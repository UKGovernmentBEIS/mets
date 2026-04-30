package uk.gov.pmrv.api.workflow.bpmn;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "workflows-type-service")
@Getter
@Setter
public class WorkflowTypeServiceProperties {

    private Set<String> flowableWorkflows = new HashSet<>();
}
