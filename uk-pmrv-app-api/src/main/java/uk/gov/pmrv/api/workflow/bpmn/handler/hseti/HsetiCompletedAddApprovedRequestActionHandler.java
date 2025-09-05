package uk.gov.pmrv.api.workflow.bpmn.handler.hseti;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETICompleteService;

@Service
@RequiredArgsConstructor
public class HsetiCompletedAddApprovedRequestActionHandler implements JavaDelegate {

    private final HSETICompleteService completeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        completeService.addApprovedRequestAction(requestId);

    }
}
