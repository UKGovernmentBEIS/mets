package uk.gov.pmrv.api.workflow.bpmn.flowable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@ConditionalOnProperty(name = "flowable.process.enabled", havingValue = "true", matchIfMissing = false)
public class FlowableDependsOnLiquibaseConfigurer implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final String liquibaseBeanName = "liquibase";
        final String flowableBeanName = "processEngine";
        
        if(!beanFactory.containsBeanDefinition(flowableBeanName)) {
        	log.warn("Flowable process engine bean not found");
        	return;
        }
        
        if(!beanFactory.containsBeanDefinition(liquibaseBeanName)) {
        	log.warn("Liquibase bean not found");
        	return;
        }
        
		beanFactory.getBeanDefinition(flowableBeanName).setDependsOn(liquibaseBeanName);
    }
}
