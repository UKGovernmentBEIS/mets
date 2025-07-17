package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.validation;


import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.validation.InstallationInspectionRequestPeerReviewValidator;


@Service
public class InstallationOnsiteInspectionRequestPeerReviewValidator
        extends InstallationInspectionRequestPeerReviewValidator {

    public InstallationOnsiteInspectionRequestPeerReviewValidator(
            PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator,
            InstallationOnsiteInspectionValidatorService installationOnsiteInspectionValidatorService) {
        super(peerReviewerTaskAssignmentValidator, installationOnsiteInspectionValidatorService);
    }

    @Override
    protected RequestType getRequestType() {
        return RequestType.INSTALLATION_ONSITE_INSPECTION;
    }
}
