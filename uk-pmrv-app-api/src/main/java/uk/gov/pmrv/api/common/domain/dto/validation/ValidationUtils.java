package uk.gov.pmrv.api.common.domain.dto.validation;

import lombok.experimental.UtilityClass;

import jakarta.validation.ConstraintValidatorContext;

@UtilityClass
public class ValidationUtils {

    public static void addConstraintViolation(ConstraintValidatorContext context, String messageTemplate, String fieldName) {
        context.buildConstraintViolationWithTemplate(messageTemplate)
            .addPropertyNode(fieldName).addBeanNode().addConstraintViolation();
    }

}
