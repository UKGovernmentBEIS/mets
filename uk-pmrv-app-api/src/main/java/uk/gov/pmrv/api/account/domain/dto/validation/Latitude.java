package uk.gov.pmrv.api.account.domain.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for latitude coordinates validation.
 */
@Constraint(validatedBy = LatitudeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Latitude {

    String message() default "Invalid latitude";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
