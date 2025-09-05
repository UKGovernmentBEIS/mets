package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;

@RequiredArgsConstructor
@Service
public class HSETIRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final HSETIValidatorService hsetiValidatorService;

    public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload taskActionPayload,
                         final AppUser appUser) {
        final HSETIApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        peerReviewerTaskAssignmentValidator
                .validate(RequestTaskType.HSE_TI_APPLICATION_PEER_REVIEW, taskActionPayload.getPeerReviewer(), appUser);

        hsetiValidatorService.validateHSETI(taskPayload.getHseti());
    }

}
