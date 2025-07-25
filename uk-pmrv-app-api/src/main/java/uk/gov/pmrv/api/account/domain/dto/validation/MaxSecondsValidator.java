package uk.gov.pmrv.api.account.domain.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.pmrv.api.account.domain.dto.CoordinatesDTO;

/**
 * The max seconds validation.
 */
public class MaxSecondsValidator implements ConstraintValidator<MaxSeconds, CoordinatesDTO> {

    /** The max seconds value */
    private int maxSeconds;

    /** {@inheritDoc} */
    @Override
    public void initialize(MaxSeconds constraintAnnotation) {
        this.maxSeconds = constraintAnnotation.seconds();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid(CoordinatesDTO coordinatesDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (coordinatesDTO == null) {
            return true;
        }
        return coordinatesDTO.getMinute() * 60 + coordinatesDTO.getSecond() <= maxSeconds;
    }
}
