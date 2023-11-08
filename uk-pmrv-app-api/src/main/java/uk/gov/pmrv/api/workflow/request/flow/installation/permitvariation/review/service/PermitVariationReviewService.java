package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewService {
    private final PermitReviewService permitReviewService;

    @Transactional
    public void savePermitVariation(PermitVariationSaveApplicationReviewRequestTaskActionPayload taskActionPayload,
                                    RequestTask requestTask) {
        PermitVariationApplicationReviewRequestTaskPayload
            requestTaskPayload = (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        permitReviewService.cleanUpDeprecatedReviewGroupDecisions(requestTaskPayload,
            taskActionPayload.getPermit().getMonitoringApproaches().getMonitoringApproaches().keySet());

        permitReviewService.resetDeterminationIfNotDeemedWithdrawn(requestTaskPayload);

        requestTaskPayload.setPermitVariationDetails(taskActionPayload.getPermitVariationDetails());
        requestTaskPayload.setPermitVariationDetailsCompleted(taskActionPayload.getPermitVariationDetailsCompleted());
        requestTaskPayload.setPermit(taskActionPayload.getPermit());
        requestTaskPayload.setPermitSectionsCompleted(taskActionPayload.getPermitSectionsCompleted());
        requestTaskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        requestTaskPayload.setPermitVariationDetailsReviewCompleted(taskActionPayload.getPermitVariationDetailsReviewCompleted());
    }

    @Transactional
    public void saveReviewGroupDecision(PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload payload, RequestTask requestTask) {
        PermitVariationApplicationReviewRequestTaskPayload
            taskPayload = (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final PermitReviewGroup group = payload.getGroup();
        final PermitVariationReviewDecision decision = payload.getDecision();

        Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();
        reviewGroupDecisions.put(group, decision);
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());

        permitReviewService.resetDeterminationIfNotValidWithDecisions(taskPayload, requestTask.getRequest().getType());
    }

    @Transactional
    public void saveDetailsReviewGroupDecision(PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload payload, RequestTask requestTask) {
        PermitVariationApplicationReviewRequestTaskPayload
            taskPayload = (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setPermitVariationDetailsReviewDecision(payload.getDecision());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        taskPayload.setPermitVariationDetailsReviewCompleted(payload.getPermitVariationDetailsReviewCompleted());

        permitReviewService.resetDeterminationIfNotValidWithDecisions(taskPayload, requestTask.getRequest().getType());
    }

    @Transactional
    public void saveDetermination(final PermitVariationSaveReviewDeterminationRequestTaskActionPayload payload,
                                  final RequestTask requestTask) {
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload = (PermitVariationApplicationReviewRequestTaskPayload) requestTask
            .getPayload();

        taskPayload.setDetermination(payload.getDetermination());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
    }
    
    @Transactional
    public void savePermitVariationDecisionNotification(RequestTask requestTask,
                                                        DecisionNotification decisionNotification,
                                                        PmrvUser pmrvUser) {
        final Request request = requestTask.getRequest();
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final PermitVariationRequestPayload requestPayload =
            (PermitVariationRequestPayload) request.getPayload();

        // update request payload
        requestPayload.setDecisionNotification(decisionNotification);
        this.updateVariationRequestPayloadWithReviewTaskPayload(requestPayload, taskPayload, pmrvUser.getUserId());
    }
    
    @Transactional
    public void saveRequestPeerReviewAction(final RequestTask requestTask,
                                            final String selectedPeerReviewer,
                                            final String pmrvUserId) {
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) requestTask.getRequest().getPayload();

        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        this.updateVariationRequestPayloadWithReviewTaskPayload(requestPayload, taskPayload, pmrvUserId);
    }
    
    @Transactional
    public void saveRequestReturnForAmends(final RequestTask requestTask, final PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        PermitVariationApplicationReviewRequestTaskPayload permitVariationApplicationReviewRequestTaskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        PermitVariationRequestPayload permitVariationRequestPayload =
            (PermitVariationRequestPayload) request.getPayload();

        updateVariationRequestPayloadWithReviewTaskPayload(
            permitVariationRequestPayload,
            permitVariationApplicationReviewRequestTaskPayload,
            pmrvUser.getUserId()
        );
    }

    private void updateVariationRequestPayloadWithReviewTaskPayload(
	        final PermitVariationRequestPayload requestPayload,
	        final PermitVariationApplicationReviewRequestTaskPayload taskPayload,
	        final String pmrvUserId) {
    	updateVariationRequestPayload(requestPayload, taskPayload);
    	requestPayload.setRegulatorReviewer(pmrvUserId);
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setPermitVariationDetailsReviewCompleted(taskPayload.getPermitVariationDetailsReviewCompleted());
        requestPayload.setReviewGroupDecisions(taskPayload.getReviewGroupDecisions());
        requestPayload.setPermitVariationDetailsReviewDecision(taskPayload.getPermitVariationDetailsReviewDecision());
        requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
        requestPayload.setDetermination(taskPayload.getDetermination());
    }
    
	private void updateVariationRequestPayload(final PermitVariationRequestPayload requestPayload,
			final PermitVariationRequestTaskPayload taskPayload) {
		requestPayload.setPermitType(taskPayload.getPermitType());
        requestPayload.setPermit(taskPayload.getPermit());
        requestPayload.setPermitVariationDetails(taskPayload.getPermitVariationDetails());
        requestPayload.setPermitSectionsCompleted(taskPayload.getPermitSectionsCompleted());
        requestPayload.setPermitVariationDetailsCompleted(taskPayload.getPermitVariationDetailsCompleted());
        requestPayload.setPermitAttachments(taskPayload.getPermitAttachments());
	}
    
}
