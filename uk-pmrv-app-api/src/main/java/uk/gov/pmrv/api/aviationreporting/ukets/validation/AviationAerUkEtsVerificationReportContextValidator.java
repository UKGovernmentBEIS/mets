package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;

public interface AviationAerUkEtsVerificationReportContextValidator {

    AviationAerValidationResult validate(AviationAerUkEtsVerificationReport verificationReport);
}
