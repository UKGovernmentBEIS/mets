package uk.gov.pmrv.api.aviationreporting.common.validation;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;

import java.util.List;

@UtilityClass
public class AviationAerValidatorHelper {

    public Object[] extractAviationAerViolations(final List<AviationAerValidationResult> aerValidationResults) {
        return aerValidationResults.stream()
            .filter(aerValidationResult -> !aerValidationResult.isValid())
            .flatMap(aerValidationResult -> aerValidationResult.getAerViolations().stream())
            .toArray();
    }
}
