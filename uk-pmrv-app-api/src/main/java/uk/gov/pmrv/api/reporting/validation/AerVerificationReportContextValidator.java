package uk.gov.pmrv.api.reporting.validation;

import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

public interface AerVerificationReportContextValidator {

    AerValidationResult validate(AerVerificationReport verificationReport, PermitOriginatedData permitOriginatedData);
}
