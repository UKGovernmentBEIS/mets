package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermitVariationSaveReviewGroupDecisionRegulatorLedActionHandler
		implements RequestTaskActionHandler<PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitVariationRegulatorLedService permitVariationRegulatorLedService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
    		PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        permitVariationRegulatorLedService.saveReviewGroupDecisionRegulatorLed(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED);
    }

}