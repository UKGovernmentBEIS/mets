package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCompleteService;

@Service
@RequiredArgsConstructor
public class AerCompletedAddRequestActionHandler implements JavaDelegate {

    private final AerCompleteService aerCompleteService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final ReviewOutcome reviewOutcome = (ReviewOutcome) execution.getVariable(BpmnProcessConstants.AER_REVIEW_OUTCOME);
        final boolean skipped = ReviewOutcome.SKIPPED.equals(reviewOutcome);
        aerCompleteService.addRequestAction(requestId, skipped);
    }
}
