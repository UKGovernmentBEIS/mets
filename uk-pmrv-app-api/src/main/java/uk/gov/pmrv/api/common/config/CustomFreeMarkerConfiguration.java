package uk.gov.pmrv.api.common.config;

import freemarker.core.TemplateClassResolver;
import no.api.freemarker.java8.Java8ObjectWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static freemarker.template.Configuration.VERSION_2_3_31;

@Configuration
public class CustomFreeMarkerConfiguration {

    @Bean
    public freemarker.template.Configuration freemarkerConfig() {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(VERSION_2_3_31);
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
        configuration.setObjectWrapper(new Java8ObjectWrapper(freemarker.template.Configuration.VERSION_2_3_31));
        return configuration;
    }
}
