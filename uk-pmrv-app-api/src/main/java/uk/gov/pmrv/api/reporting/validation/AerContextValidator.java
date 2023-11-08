package uk.gov.pmrv.api.reporting.validation;

import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;

import jakarta.validation.Valid;

public interface AerContextValidator {

    AerValidationResult validate(@Valid AerContainer aerContainer);
}
