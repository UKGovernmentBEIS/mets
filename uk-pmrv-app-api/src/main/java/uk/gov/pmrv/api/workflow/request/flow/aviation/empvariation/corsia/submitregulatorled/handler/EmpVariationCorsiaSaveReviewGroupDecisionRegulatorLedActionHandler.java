package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaSubmitRegulatorLedService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Component
@RequiredArgsConstructor
public class EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedActionHandler implements
		RequestTaskActionHandler<EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final EmpVariationCorsiaSubmitRegulatorLedService regulatorLedService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final AppUser appUser,
                        final EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        regulatorLedService.saveReviewGroupDecision(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED);
    }

}