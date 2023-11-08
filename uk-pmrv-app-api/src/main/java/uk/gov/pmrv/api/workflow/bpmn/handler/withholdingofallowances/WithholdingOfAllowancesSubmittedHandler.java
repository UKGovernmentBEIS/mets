package uk.gov.pmrv.api.workflow.bpmn.handler.withholdingofallowances;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesSubmittedService;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesSubmittedHandler implements JavaDelegate {

    private final WithholdingOfAllowancesSubmittedService service;

    @Override
    public void execute(final DelegateExecution execution) {
        service.submit((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
