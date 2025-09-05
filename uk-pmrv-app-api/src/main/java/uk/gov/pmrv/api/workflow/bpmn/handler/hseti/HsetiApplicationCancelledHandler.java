package uk.gov.pmrv.api.workflow.bpmn.handler.hseti;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETISubmitService;


@Service
@RequiredArgsConstructor
public class HsetiApplicationCancelledHandler implements JavaDelegate {

    private final HSETISubmitService submitService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        submitService.cancel(requestId);
    }
}
