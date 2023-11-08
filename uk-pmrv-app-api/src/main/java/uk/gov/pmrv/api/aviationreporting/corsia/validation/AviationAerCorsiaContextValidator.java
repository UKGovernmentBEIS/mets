package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import jakarta.validation.Valid;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;

public interface AviationAerCorsiaContextValidator {

    AviationAerValidationResult validate(@Valid AviationAerCorsiaContainer aerContainer);
}
