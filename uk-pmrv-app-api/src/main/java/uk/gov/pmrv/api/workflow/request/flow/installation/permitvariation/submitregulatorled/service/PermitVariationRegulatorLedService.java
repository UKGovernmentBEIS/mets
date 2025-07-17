package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermitVariationRegulatorLedService {

    @Transactional
    public void saveReviewGroupDecisionRegulatorLed(PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload requestTaskActionPayload, RequestTask requestTask) {
    	PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload
            requestTaskPayload = (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
    	
        Map<PermitReviewGroup, PermitAcceptedVariationDecisionDetails> reviewGroupDecisions = requestTaskPayload.getReviewGroupDecisions();
        reviewGroupDecisions.put(requestTaskActionPayload.getGroup(), requestTaskActionPayload.getDecision());
    }

    @Transactional
    public void saveDetailsReviewGroupDecisionRegulatorLed(PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload payload, RequestTask requestTask) {
    	PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload
            taskPayload = (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();

        taskPayload.setPermitVariationDetailsReviewDecision(payload.getDecision());
    }

    @Transactional
    public void saveDeterminationRegulatorLed(final PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload actionPayload,
                                  final RequestTask requestTask) {
        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask
            .getPayload();
        taskPayload.setDetermination(actionPayload.getDetermination());
        taskPayload.setReviewSectionsCompleted(actionPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void savePermitVariationDecisionNotificationRegulatorLed(RequestTask requestTask,
                                                        DecisionNotification decisionNotification,
                                                        AppUser appUser) {
        final Request request = requestTask.getRequest();
        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();

        final PermitVariationRequestPayload requestPayload =
            (PermitVariationRequestPayload) request.getPayload();

        // update request payload
        requestPayload.setDecisionNotification(decisionNotification);
        
        updateVariationRequestPayloadWithRegulatorLedTaskPayload(requestPayload, taskPayload, appUser.getUserId());
    }

    @Transactional
    public void saveRequestPeerReviewActionRegulatorLed(final RequestTask requestTask,
                                            final String selectedPeerReviewer,
                                            final String appUserId) {
        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();

        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) requestTask.getRequest().getPayload();

        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        updateVariationRequestPayloadWithRegulatorLedTaskPayload(requestPayload, taskPayload, appUserId);
    }

    private void updateVariationRequestPayloadWithRegulatorLedTaskPayload(
	        final PermitVariationRequestPayload requestPayload,
	        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload,
	        final String appUserId) {
    	updateVariationRequestPayload(requestPayload, taskPayload);
		requestPayload.setRegulatorReviewer(appUserId);
		requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
		requestPayload.setReviewGroupDecisionsRegulatorLed(taskPayload.getReviewGroupDecisions());
		requestPayload.setPermitVariationDetailsReviewDecisionRegulatorLed(taskPayload.getPermitVariationDetailsReviewDecision());
		requestPayload.setDeterminationRegulatorLed(taskPayload.getDetermination());
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
