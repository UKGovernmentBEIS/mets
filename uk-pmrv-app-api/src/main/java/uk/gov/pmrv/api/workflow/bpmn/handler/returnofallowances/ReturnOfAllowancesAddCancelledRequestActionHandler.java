package uk.gov.pmrv.api.workflow.bpmn.handler.returnofallowances;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesApplicationCancelledService;

@Service
@RequiredArgsConstructor
public class ReturnOfAllowancesAddCancelledRequestActionHandler implements JavaDelegate {

    private final ReturnOfAllowancesApplicationCancelledService service;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        service.cancel((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
