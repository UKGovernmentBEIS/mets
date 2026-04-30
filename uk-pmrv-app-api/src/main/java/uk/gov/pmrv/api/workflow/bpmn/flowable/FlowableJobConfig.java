package uk.gov.pmrv.api.workflow.bpmn.flowable;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConditionalOnProperty(name = "flowable.process.enabled", havingValue = "true", matchIfMissing = false)
public class FlowableJobConfig {
	
	private final FlowableJobProperties props;

	FlowableJobConfig(FlowableJobProperties props) {
        this.props = props;
    }
	
    @Bean
    @ConfigurationProperties(prefix = "flowable-job")
    org.springframework.core.task.AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(props.getCorePoolSize());
        executor.setMaxPoolSize(props.getMaxPoolSize());
        executor.setQueueCapacity(props.getQueueCapacity());
        executor.setThreadNamePrefix("flowable-atask-executor-");
        executor.setAwaitTerminationSeconds(30);
        executor.setWaitForTasksToCompleteOnShutdown(false);
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }
    
}
