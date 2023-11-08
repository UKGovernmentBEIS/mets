package uk.gov.pmrv.api.common.config;

import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ActuatorConfig {

    @Bean
    public DataSourceHealthIndicator requiredPrimaryDataSourceHealthIndicator(DataSource primaryDataSource) {
        return new DataSourceHealthIndicator(primaryDataSource);
    }

    @Bean
    public DataSourceHealthIndicator requiredReportEaDataSourceHealthIndicator(DataSource reportEaDataSource) {
        return new DataSourceHealthIndicator(reportEaDataSource);
    }

    @Bean
    public DataSourceHealthIndicator requiredReportSepaDataSourceHealthIndicator(DataSource reportSepaDataSource) {
        return new DataSourceHealthIndicator(reportSepaDataSource);
    }

    @Bean
    public DataSourceHealthIndicator requiredReportNieaDataSourceHealthIndicator(DataSource reportNieaDataSource) {
        return new DataSourceHealthIndicator(reportNieaDataSource);
    }

    @Bean
    public DataSourceHealthIndicator requiredReportOpredDataSourceHealthIndicator(DataSource reportOpredDataSource) {
        return new DataSourceHealthIndicator(reportOpredDataSource);
    }

    @Bean
    public DataSourceHealthIndicator requiredReportNrwDataSourceHealthIndicator(DataSource reportNrwDataSource) {
        return new DataSourceHealthIndicator(reportNrwDataSource);
    }
}
