package uk.gov.pmrv.api.notification.template;

import static freemarker.template.Configuration.VERSION_2_3_31;

import fr.opensagres.xdocreport.document.docx.discovery.DocxTemplateEngineConfiguration;
import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import freemarker.core.TemplateClassResolver;
import no.api.freemarker.java8.Java8ObjectWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplatesConfiguration {

    @Bean
    public FreemarkerTemplateEngine freemarkerTemplateEngine(freemarker.template.Configuration templateFreemarkerConfig) {
        FreemarkerTemplateEngine freemarkerTemplateEngine = new FreemarkerTemplateEngine();
        freemarkerTemplateEngine.setFreemarkerConfiguration(templateFreemarkerConfig);
        freemarkerTemplateEngine.setConfiguration( DocxTemplateEngineConfiguration.INSTANCE );
        return freemarkerTemplateEngine;
    }

    @Bean
    public freemarker.template.Configuration templateFreemarkerConfig() {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(VERSION_2_3_31);
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
        configuration.setObjectWrapper(new Java8ObjectWrapper(freemarker.template.Configuration.VERSION_2_3_31));
        return configuration;
    }
}
