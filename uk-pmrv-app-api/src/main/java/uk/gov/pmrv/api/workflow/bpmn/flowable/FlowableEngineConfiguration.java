package uk.gov.pmrv.api.workflow.bpmn.flowable;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
@ConditionalOnProperty(name = "flowable.process.enabled", havingValue = "true", matchIfMissing = false)
public class FlowableEngineConfiguration implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
	private final DataSource flowableDataSource;
	private final PlatformTransactionManager flowableTransactionManager;

    public FlowableEngineConfiguration(@Qualifier(value="flowableDataSource") DataSource flowableDataSource, PlatformTransactionManager flowableTransactionManager) {
        this.flowableDataSource = flowableDataSource;
        this.flowableTransactionManager = flowableTransactionManager;
    }

    @Override
	public void configure(SpringProcessEngineConfiguration engineConfiguration) {
		engineConfiguration.setDataSource(flowableDataSource);
		engineConfiguration.setTransactionManager(flowableTransactionManager);
	}
}
