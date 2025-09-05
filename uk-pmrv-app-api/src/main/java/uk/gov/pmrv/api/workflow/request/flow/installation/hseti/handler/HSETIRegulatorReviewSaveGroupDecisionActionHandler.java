package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIRegulatorReviewSubmitService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HSETIRegulatorReviewSaveGroupDecisionActionHandler implements RequestTaskActionHandler<HSETIApplicationSaveRegulatorReviewGroupDecisionRequestTaskActionPayload>{

    private final RequestTaskService requestTaskService;
    private final HSETIRegulatorReviewSubmitService hsetiRegulatorReviewSubmitService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        HSETIApplicationSaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        hsetiRegulatorReviewSubmitService.saveReviewGroupDecision(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.HSE_TI_SAVE_REGULATOR_REVIEW_GROUP_DECISION);
    }

}