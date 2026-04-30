package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.rde;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeTerminatedService;

@Service
@RequiredArgsConstructor
public class RdeTerminatedHandlerFlowable implements JavaDelegate {

    private final RdeTerminatedService service;

    @Override
    public void execute(DelegateExecution execution) {
        
        execution.removeVariable(BpmnProcessConstants.RDE_EXPIRATION_DATE);
        execution.removeVariable(BpmnProcessConstants.RDE_FIRST_REMINDER_DATE);
        execution.removeVariable(BpmnProcessConstants.RDE_SECOND_REMINDER_DATE);
        execution.removeVariable(BpmnProcessConstants.RDE_OUTCOME);

        service.terminate((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
