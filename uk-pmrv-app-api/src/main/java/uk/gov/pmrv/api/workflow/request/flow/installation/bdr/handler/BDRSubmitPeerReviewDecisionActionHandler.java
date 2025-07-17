package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.PeerReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDROutcome;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BDRSubmitPeerReviewDecisionActionHandler implements RequestTaskActionHandler<PeerReviewDecisionRequestTaskActionPayload> {

    private static final PeerReviewMapper PEER_REVIEW_MAPPER = Mappers.getMapper(PeerReviewMapper.class);

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, PeerReviewDecisionRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String peerReviewer = appUser.getUserId();

        final PeerReviewDecisionSubmittedRequestActionPayload actionPayload = PEER_REVIEW_MAPPER
                .toPeerReviewDecisionSubmittedRequestActionPayload(taskActionPayload,
                        RequestActionPayloadType.BDR_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD);

        final RequestActionType type = actionPayload.getDecision().getType() == PeerReviewDecisionType.AGREE
                ? RequestActionType.BDR_APPLICATION_PEER_REVIEW_ACCEPTED
                : RequestActionType.BDR_APPLICATION_PEER_REVIEW_REJECTED;

        requestService.addActionToRequest(request, actionPayload, type, peerReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.BDR_OUTCOME,
                BDROutcome.SUBMITTED_TO_REGULATOR
        ));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.BDR_SUBMIT_PEER_REVIEW_DECISION);
    }
}
