package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.PeerReviewMapper;

@Component
@RequiredArgsConstructor
public class EmpVariationCorsiaPeerReviewDecisionActionHandler
    implements RequestTaskActionHandler<PeerReviewDecisionRequestTaskActionPayload> {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

    private static final PeerReviewMapper PEER_REVIEW_MAPPER = Mappers.getMapper(PeerReviewMapper.class);

    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        PeerReviewDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String peerReviewer = appUser.getUserId();

        final PeerReviewDecisionSubmittedRequestActionPayload actionPayload =
            PEER_REVIEW_MAPPER.toPeerReviewDecisionSubmittedRequestActionPayload(
                payload,
                RequestActionPayloadType.EMP_VARIATION_CORSIA_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD
            );

        final RequestActionType type = actionPayload.getDecision().getType() == PeerReviewDecisionType.AGREE ?
            RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED :
            RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED;

        requestService.addActionToRequest(request,
            actionPayload,
            type,
            peerReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION);
    }

}
