package uk.gov.pmrv.api.reporting.validation;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;

import java.util.List;

@UtilityClass
public class AerValidatorHelper {

    public Object[] extractAerViolations(List<AerValidationResult> aerValidationResults) {
        return aerValidationResults.stream()
            .filter(aerValidationResult -> !aerValidationResult.isValid())
            .flatMap(aerValidationResult -> aerValidationResult.getAerViolations().stream())
            .toArray();
    }

}
