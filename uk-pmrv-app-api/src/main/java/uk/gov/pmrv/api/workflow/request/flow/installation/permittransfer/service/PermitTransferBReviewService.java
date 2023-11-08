package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class PermitTransferBReviewService {

    private final PermitReviewService permitReviewService;

    @Transactional
    public void saveDetailsConfirmationReviewGroupDecision(
        final PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload payload,
        final RequestTask requestTask) {

        final PermitTransferBApplicationReviewRequestTaskPayload
            taskPayload = (PermitTransferBApplicationReviewRequestTaskPayload) requestTask.getPayload();
        taskPayload.setPermitTransferDetailsConfirmationDecision(payload.getDecision());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());

        final RequestType requestType = requestTask.getRequest().getType();
        permitReviewService.resetDeterminationIfNotValidWithDecisions(taskPayload, requestType);
    }


    @Transactional
    public void saveRequestPeerReviewAction(final RequestTask requestTask,
                                            final String selectedPeerReview,
                                            final PmrvUser pmrvUser) {

        final PermitTransferBRequestPayload requestPayload = this.updatePermitTransferBRequestPayload(requestTask, pmrvUser);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReview);
    }
    

    @Transactional
    public PermitTransferBRequestPayload updatePermitTransferBRequestPayload(final RequestTask requestTask,
                                                                             final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload =
            (PermitTransferBApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        requestPayload.setPermitType(taskPayload.getPermitType());
        requestPayload.setPermit(taskPayload.getPermit());
        requestPayload.setPermitSectionsCompleted(taskPayload.getPermitSectionsCompleted());
        requestPayload.setPermitAttachments(taskPayload.getPermitAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setReviewGroupDecisions(taskPayload.getReviewGroupDecisions());
        requestPayload.setPermitTransferDetailsConfirmationDecision(taskPayload.getPermitTransferDetailsConfirmationDecision());
        requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
        requestPayload.setDetermination(taskPayload.getDetermination());
        
        return requestPayload;
    }
    
    @Transactional
    public void savePermitTransferBDecisionNotification(final RequestTask requestTask,
                                                        final DecisionNotification permitDecisionNotification,
                                                        final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();
        requestPayload.setDecisionNotification(permitDecisionNotification);
        this.updatePermitTransferBRequestPayload(requestTask, pmrvUser);
    }
}
