package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirReviewService;

@Service
@RequiredArgsConstructor
public class AirAddReviewedRequestActionHandlerFlowable implements JavaDelegate {

    private final AirReviewService service;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.addReviewedRequestAction(requestId);
    }
}
