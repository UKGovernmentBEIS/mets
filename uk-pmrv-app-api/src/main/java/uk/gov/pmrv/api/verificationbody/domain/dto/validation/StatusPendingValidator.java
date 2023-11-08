package uk.gov.pmrv.api.verificationbody.domain.dto.validation;

import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusPendingValidator implements ConstraintValidator<StatusPending, VerificationBodyStatus> {

    @Override
    public boolean isValid(VerificationBodyStatus status, ConstraintValidatorContext context) {
        return !VerificationBodyStatus.PENDING.equals(status);
    }
}
