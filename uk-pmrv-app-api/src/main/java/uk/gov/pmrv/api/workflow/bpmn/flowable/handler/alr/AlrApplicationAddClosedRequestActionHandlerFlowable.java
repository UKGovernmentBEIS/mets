package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRCloseService;

@Service
@RequiredArgsConstructor
public class AlrApplicationAddClosedRequestActionHandlerFlowable implements JavaDelegate {

    private final ALRCloseService alrCloseService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        alrCloseService.addClosedRequestAction(requestId);
    }
}
