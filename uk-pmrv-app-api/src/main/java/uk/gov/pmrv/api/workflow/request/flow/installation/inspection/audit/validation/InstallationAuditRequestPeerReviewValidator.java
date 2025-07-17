package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.validation.InstallationInspectionRequestPeerReviewValidator;


@Service
public class InstallationAuditRequestPeerReviewValidator
        extends InstallationInspectionRequestPeerReviewValidator {

    public InstallationAuditRequestPeerReviewValidator(
            PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator,
            InstallationAuditValidatorService installationAuditValidatorService) {
        super(peerReviewerTaskAssignmentValidator, installationAuditValidatorService);
    }

    @Override
    protected RequestType getRequestType() {
        return RequestType.INSTALLATION_AUDIT;
    }
}