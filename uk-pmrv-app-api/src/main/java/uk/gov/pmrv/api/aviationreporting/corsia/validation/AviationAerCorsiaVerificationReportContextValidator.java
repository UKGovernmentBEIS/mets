package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;

public interface AviationAerCorsiaVerificationReportContextValidator {

    AviationAerValidationResult validate(AviationAerCorsiaVerificationReport verificationReport, AviationAerCorsia aer);
}
