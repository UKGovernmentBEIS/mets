package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permanentcessation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationSubmittedService;

@Service
@RequiredArgsConstructor
public class PermanentCessationApplicationSubmittedHandlerFlowable implements JavaDelegate {

    private final PermanentCessationSubmittedService permanentCessationSubmittedService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        permanentCessationSubmittedService.submit(requestId);
    }
}
