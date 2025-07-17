package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.PeerReviewMapper;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsReviewPeerDecisionActionHandler 
	implements RequestTaskActionHandler<PeerReviewDecisionRequestTaskActionPayload> {

	private static final PeerReviewMapper PEER_REVIEW_MAPPER = Mappers.getMapper(PeerReviewMapper.class);

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        AppUser appUser, PeerReviewDecisionRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        PeerReviewDecisionSubmittedRequestActionPayload requestActionPayload =
            PEER_REVIEW_MAPPER.toPeerReviewDecisionSubmittedRequestActionPayload(
                payload,
                RequestActionPayloadType.EMP_VARIATION_UKETS_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD
            );

        RequestActionType type = payload.getDecision().getType() == PeerReviewDecisionType.AGREE ?
            RequestActionType.EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED :
            RequestActionType.EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_REJECTED;

        requestService.addActionToRequest(requestTask.getRequest(),
            requestActionPayload,
            type,
            appUser.getUserId());

        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION);
    }
}
