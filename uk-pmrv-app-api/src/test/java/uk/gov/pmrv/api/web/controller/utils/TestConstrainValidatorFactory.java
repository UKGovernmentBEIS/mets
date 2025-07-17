package uk.gov.pmrv.api.web.controller.utils;

import jakarta.validation.ConstraintValidator;
import org.springframework.web.bind.support.SpringWebConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.netz.api.referencedata.service.CountryValidator;

public class TestConstrainValidatorFactory extends SpringWebConstraintValidatorFactory {

    private final WebApplicationContext ctx;

    public TestConstrainValidatorFactory(WebApplicationContext ctx) {
        this.ctx = ctx;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        if(key == CountryValidator.class) {
            return (T)ctx.getBean(CountryValidator.class);
        } else {
            return (T)super.getInstance(key);
        }
    }

    @Override
    protected WebApplicationContext getWebApplicationContext() {
        return ctx;
    }

}