package uk.gov.pmrv.api.mireport.common;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
public class ReportDatasourceConfig {
    private static final  String PACKAGE = "uk.gov.pmrv.api";

    @Bean
    @ConfigurationProperties(prefix="report-datasource-ea")
    public DataSourceProperties reportEaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "report-datasource-ea.hikari")
    public HikariDataSource reportEaDataSource() {
        return reportEaDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "reportEaEntityManager")
    public LocalContainerEntityManagerFactoryBean reportEaEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(reportEaDataSource())
                .persistenceUnit("reportEa")
                .packages(PACKAGE)
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix="report-datasource-sepa")
    public DataSourceProperties reportSepaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "report-datasource-sepa.hikari")
    public HikariDataSource reportSepaDataSource() {
        return reportSepaDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "reportSepaEntityManager")
    public LocalContainerEntityManagerFactoryBean reportSepaEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(reportSepaDataSource())
                .persistenceUnit("reportSepa")
                .packages(PACKAGE)
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix="report-datasource-niea")
    public DataSourceProperties reportNieaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "report-datasource-niea.hikari")
    public HikariDataSource reportNieaDataSource() {
        return reportNieaDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "reportNieaEntityManager")
    public LocalContainerEntityManagerFactoryBean reportNieaEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(reportNieaDataSource())
                .persistenceUnit("reportNiea")
                .packages(PACKAGE)
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix="report-datasource-opred")
    public DataSourceProperties reportOpredDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "report-datasource-opred.hikari")
    public HikariDataSource reportOpredDataSource() {
        return reportOpredDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "reportOpredEntityManager")
    public LocalContainerEntityManagerFactoryBean reportOpredEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(reportOpredDataSource())
                .persistenceUnit("reportOpred")
                .packages(PACKAGE)
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix="report-datasource-nrw")
    public DataSourceProperties reportNrwDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "report-datasource-nrw.hikari")
    public HikariDataSource reportNrwDataSource() {
        return reportNrwDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "reportNrwEntityManager")
    public LocalContainerEntityManagerFactoryBean reportNrwEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(reportNrwDataSource())
                .persistenceUnit("reportNrw")
                .packages(PACKAGE)
                .build();
    }
}
