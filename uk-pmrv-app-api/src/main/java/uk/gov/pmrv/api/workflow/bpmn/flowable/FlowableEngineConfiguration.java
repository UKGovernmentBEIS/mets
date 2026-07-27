package uk.gov.pmrv.api.workflow.bpmn.flowable;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Component
@ConditionalOnProperty(name = "flowable.process.enabled", havingValue = "true", matchIfMissing = false)
public class FlowableEngineConfiguration implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final String databaseSchema;

    public FlowableEngineConfiguration(DataSource dataSource, PlatformTransactionManager transactionManager,
                                       @Value("${flowable.database-schema:}") String databaseSchema) {
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
        this.databaseSchema = databaseSchema;
    }

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setDataSource(dataSource);
        engineConfiguration.setTransactionManager(transactionManager);
        engineConfiguration.setDatabaseSchema(null);

        if (StringUtils.hasText(databaseSchema)) {
            engineConfiguration.setDatabaseTablePrefix(databaseSchema.trim() + ".");
            engineConfiguration.setTablePrefixIsSchema(true);
        }
    }
}
