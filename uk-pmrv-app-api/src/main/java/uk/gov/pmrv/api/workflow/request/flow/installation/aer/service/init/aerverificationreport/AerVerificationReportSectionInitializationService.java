package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.aerverificationreport;

import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;

public interface AerVerificationReportSectionInitializationService {

    void initialize(AerVerificationData aerVerificationData, Aer aer);
}
