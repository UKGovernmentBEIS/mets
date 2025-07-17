package uk.gov.pmrv.api.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.pmrv.api.account.transform.StringToAccountTypeEnumConverter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AppUserArgumentResolver appUserArgumentResolver;
    private final StringToAccountTypeEnumConverter stringToAccountTypeEnumConverter;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(appUserArgumentResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToAccountTypeEnumConverter);
    }
}
