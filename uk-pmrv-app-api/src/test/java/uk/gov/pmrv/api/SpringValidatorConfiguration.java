package uk.gov.pmrv.api;

import org.hibernate.validator.HibernateValidator;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import uk.gov.netz.api.referencedata.service.CountryService;

import jakarta.validation.Validator;

@TestConfiguration
@Import(value = CountryService.class)
public class SpringValidatorConfiguration {

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setProviderClass(HibernateValidator.class);
        bean.afterPropertiesSet();

        return bean;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(final Validator validator) {
        return hibernateProperties -> hibernateProperties.put("jakarta.persistence.validation.factory", validator);
    }
}
