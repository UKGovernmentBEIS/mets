package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.enumeration.AviationAerCorsiaAnnualOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.service.AviationAerCorsiaAnnualOffsettingRequestPeerReviewValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingPeerReviewActionHandler
        implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final AviationAerCorsiaAnnualOffsettingSubmitService aviationAerCorsiaAnnualOffsettingSubmitService;
    private final AviationAerCorsiaAnnualOffsettingRequestPeerReviewValidator aviationAerCorsiaAnnualOffsettingRequestPeerReviewValidator;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, PeerReviewRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = appUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        aviationAerCorsiaAnnualOffsettingRequestPeerReviewValidator.validate(requestTask, taskActionPayload, appUser);
        aviationAerCorsiaAnnualOffsettingSubmitService.requestPeerReview(requestTask, peerReviewer, appUser);

        requestService.addActionToRequest(request,
                null,
                RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED,
                userId);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                        BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME,
                        AviationAerCorsiaAnnualOffsettingSubmitOutcome.PEER_REVIEW_REQUIRED
                )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_REQUEST_PEER_REVIEW);
    }
}
