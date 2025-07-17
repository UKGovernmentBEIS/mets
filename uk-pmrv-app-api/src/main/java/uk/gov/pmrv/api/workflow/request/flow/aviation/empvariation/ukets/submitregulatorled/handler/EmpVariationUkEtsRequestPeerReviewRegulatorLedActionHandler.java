package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsRegulatorLedPeerReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.validator.EmpVariationUkEtsRequestPeerReviewRegulatorLedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsRequestPeerReviewRegulatorLedActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final EmpVariationUkEtsRegulatorLedPeerReviewService empVariationUkEtsRegulatorLedPeerReviewService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final EmpVariationUkEtsRequestPeerReviewRegulatorLedValidator validator;
    
    @Override
    public void process(final Long requestTaskId, 
    		final RequestTaskActionType requestTaskActionType, 
            final AppUser appUser,
            final PeerReviewRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = appUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        validator.validate(requestTask, taskActionPayload, appUser);
        empVariationUkEtsRegulatorLedPeerReviewService.saveRequestPeerReviewAction(requestTask, peerReviewer, userId);
        requestService.addActionToRequest(request, null, RequestActionType.EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED, userId);
        
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.EMP_VARIATION_SUBMIT_OUTCOME, EmpVariationSubmitOutcome.SUBMITTED,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_REGULATOR_LED);
    }
}
