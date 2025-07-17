package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service.RequestAviationAerCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AviationAerCorsiaReviewSaveGroupDecisionActionHandler
    implements RequestTaskActionHandler<AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAviationAerCorsiaReviewService aviationAerCorsiaReviewService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        aviationAerCorsiaReviewService.saveReviewGroupDecision(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_REVIEW_GROUP_DECISION);
    }
}
