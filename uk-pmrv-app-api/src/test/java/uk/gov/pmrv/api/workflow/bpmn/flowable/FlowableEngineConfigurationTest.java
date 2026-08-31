package uk.gov.pmrv.api.workflow.bpmn.flowable;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class FlowableEngineConfigurationTest {

    private final DataSource dataSource = mock(DataSource.class);
    private final PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);

    @Test
    void configure_with_database_schema() {
        FlowableEngineConfiguration configuration =
                new FlowableEngineConfiguration(dataSource, transactionManager, "sch_flowable");
        SpringProcessEngineConfiguration engineConfiguration = new SpringProcessEngineConfiguration();
        engineConfiguration.setDatabaseSchema("sch_flowable");

        configuration.configure(engineConfiguration);

        assertEquals("sch_flowable.", engineConfiguration.getDatabaseTablePrefix());
        assertTrue(engineConfiguration.isTablePrefixIsSchema());
        assertEquals("sch_flowable", engineConfiguration.getDatabaseSchema());
        TransactionAwareDataSourceProxy configuredDataSource =
                (TransactionAwareDataSourceProxy) engineConfiguration.getDataSource();
        assertSame(dataSource, configuredDataSource.getTargetDataSource());
        assertSame(transactionManager, engineConfiguration.getTransactionManager());
    }

    @Test
    void configure_with_blank_database_schema() {
        FlowableEngineConfiguration configuration =
                new FlowableEngineConfiguration(dataSource, transactionManager, "");
        SpringProcessEngineConfiguration engineConfiguration = new SpringProcessEngineConfiguration();
        engineConfiguration.setDatabaseSchema("");

        configuration.configure(engineConfiguration);

        assertEquals("", engineConfiguration.getDatabaseTablePrefix());
        assertFalse(engineConfiguration.isTablePrefixIsSchema());
        assertEquals("", engineConfiguration.getDatabaseSchema());
    }

    @Test
    void configure_with_missing_database_schema() {
        FlowableEngineConfiguration configuration =
                new FlowableEngineConfiguration(dataSource, transactionManager, null);
        SpringProcessEngineConfiguration engineConfiguration = new SpringProcessEngineConfiguration();

        configuration.configure(engineConfiguration);

        assertEquals("", engineConfiguration.getDatabaseTablePrefix());
        assertFalse(engineConfiguration.isTablePrefixIsSchema());
        assertNull(engineConfiguration.getDatabaseSchema());
    }
}
