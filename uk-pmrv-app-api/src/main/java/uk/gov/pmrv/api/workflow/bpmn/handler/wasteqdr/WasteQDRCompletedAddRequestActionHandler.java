package uk.gov.pmrv.api.workflow.bpmn.handler.wasteqdr;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRCompleteService;

@Service
@RequiredArgsConstructor
public class WasteQDRCompletedAddRequestActionHandler implements JavaDelegate {

    private final WasteQDRCompleteService completeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        completeService.addRequestAction(requestId);
    }
}
