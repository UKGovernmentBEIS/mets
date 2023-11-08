package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import java.util.List;

import java.util.Map;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.PeerReviewMapper;

@Component
@RequiredArgsConstructor
public class AviationDreUkEtsReviewPeerDecisionActionHandler
    implements RequestTaskActionHandler<PeerReviewDecisionRequestTaskActionPayload> {

    private static final PeerReviewMapper PEER_REVIEW_MAPPER = Mappers.getMapper(PeerReviewMapper.class);

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PeerReviewDecisionRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String peerReviewer = pmrvUser.getUserId();

        final PeerReviewDecisionSubmittedRequestActionPayload actionPayload = PEER_REVIEW_MAPPER
            .toPeerReviewDecisionSubmittedRequestActionPayload(taskActionPayload,
                RequestActionPayloadType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD);

        final RequestActionType type = actionPayload.getDecision().getType() == PeerReviewDecisionType.AGREE
            ? RequestActionType.AVIATION_DRE_UKETS_PEER_REVIEWER_ACCEPTED
            : RequestActionType.AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED;

        requestService.addActionToRequest(request, actionPayload, type, peerReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.DRE_SUBMIT_OUTCOME, AviationDreSubmitOutcome.SUBMITTED
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION);
    }

}
