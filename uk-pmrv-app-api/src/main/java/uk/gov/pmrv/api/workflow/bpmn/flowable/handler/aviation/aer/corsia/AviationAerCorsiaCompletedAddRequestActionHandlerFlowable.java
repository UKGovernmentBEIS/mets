package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service.AviationAerCorsiaCompleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaCompletedAddRequestActionHandlerFlowable implements JavaDelegate {

    private final AviationAerCorsiaCompleteService aviationAerCorsiaCompleteService;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final ReviewOutcome reviewOutcome = (ReviewOutcome) execution.getVariable(BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME);
        final boolean skipped = ReviewOutcome.SKIPPED.equals(reviewOutcome);
        aviationAerCorsiaCompleteService.addRequestAction(requestId, skipped);
    }
}
