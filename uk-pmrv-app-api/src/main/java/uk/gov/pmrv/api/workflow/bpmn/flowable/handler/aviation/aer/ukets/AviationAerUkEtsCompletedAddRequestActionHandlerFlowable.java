package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service.AviationAerUkEtsCompleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsCompletedAddRequestActionHandlerFlowable implements JavaDelegate {

    private final AviationAerUkEtsCompleteService aviationAerUkEtsCompleteService;

    @Override
    public void execute(DelegateExecution execution) {
        
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final ReviewOutcome reviewOutcome = (ReviewOutcome) execution.getVariable(BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME);
        final boolean skipped = ReviewOutcome.SKIPPED.equals(reviewOutcome);
        aviationAerUkEtsCompleteService.addRequestAction(requestId, skipped);
    }
}
