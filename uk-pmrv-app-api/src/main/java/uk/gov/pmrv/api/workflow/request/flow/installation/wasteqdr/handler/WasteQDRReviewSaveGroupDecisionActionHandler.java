package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRReviewService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WasteQDRReviewSaveGroupDecisionActionHandler implements RequestTaskActionHandler<WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WasteQDRReviewService wasteQDRReviewService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        wasteQDRReviewService.saveReviewDecision(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WASTE_QDR_SAVE_REVIEW_GROUP_DECISION);
    }
}
