package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.ukets;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service.AviationAerUkEtsCompleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsCompletedAddRequestActionHandler implements JavaDelegate {

    private final AviationAerUkEtsCompleteService aviationAerUkEtsCompleteService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final ReviewOutcome reviewOutcome = (ReviewOutcome) execution.getVariable(BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME);
        final boolean skipped = ReviewOutcome.SKIPPED.equals(reviewOutcome);
        aviationAerUkEtsCompleteService.addRequestAction(requestId, skipped);
    }
}
