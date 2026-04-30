package uk.gov.pmrv.api.workflow.bpmn.flowable;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "flowableEntityManagerFactory",
        transactionManagerRef = "flowableTransactionManager",
        basePackages = "org.flowable"
)
@ConditionalOnProperty(name = "flowable.process.enabled", havingValue = "true", matchIfMissing = false)
public class FlowableDatasourceConfig {
	
    @Bean(defaultCandidate = false)
    @ConfigurationProperties(prefix="flowable-datasource")
    DataSourceProperties flowableDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "flowableDataSource", defaultCandidate = false)
    @ConfigurationProperties(prefix = "flowable-datasource.hikari")
    HikariDataSource flowableDataSource() {
        return flowableDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(defaultCandidate = false)
    LocalContainerEntityManagerFactoryBean flowableEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            JpaProperties jpaProperties,
            HibernateProperties hibernateProperties,
            ConfigurableListableBeanFactory beanFactory) {
        LocalContainerEntityManagerFactoryBean emfb = builder
                .dataSource(flowableDataSource())
                .packages("org.flowable")
                .properties(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()))
                .build();
        emfb.getJpaPropertyMap().put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
        return emfb;
    }

    @Bean(defaultCandidate = false)
    PlatformTransactionManager flowableTransactionManager(EntityManagerFactory flowableEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(flowableDataSource());
        transactionManager.setEntityManagerFactory(flowableEntityManagerFactory);
        return transactionManager;
    }
    
}
