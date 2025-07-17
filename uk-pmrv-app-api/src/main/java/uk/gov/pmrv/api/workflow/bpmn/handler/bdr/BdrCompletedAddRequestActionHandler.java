package uk.gov.pmrv.api.workflow.bpmn.handler.bdr;


import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRCompleteService;

@Service
@RequiredArgsConstructor
public class BdrCompletedAddRequestActionHandler implements JavaDelegate {

    private final BDRCompleteService bdrCompleteService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        bdrCompleteService.addRequestAction(requestId);

    }
}
