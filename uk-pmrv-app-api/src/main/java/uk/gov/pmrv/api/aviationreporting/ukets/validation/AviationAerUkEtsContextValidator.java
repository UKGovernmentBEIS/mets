package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import jakarta.validation.Valid;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;

public interface AviationAerUkEtsContextValidator {

    AviationAerValidationResult validate(@Valid AviationAerUkEtsContainer aerContainer);
}
