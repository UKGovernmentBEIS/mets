package uk.gov.pmrv.api.permit.validation;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;

import java.util.List;

@UtilityClass
public class PermitValidatorHelper {

    public Object[] extractPermitViolations(List<PermitValidationResult> permitContextValidatorResults) {
        return permitContextValidatorResults.stream()
            .filter(permitValidationResult -> !permitValidationResult.isValid())
            .flatMap(permitValidationResult -> permitValidationResult.getPermitViolations().stream())
            .toArray();
    }
}
