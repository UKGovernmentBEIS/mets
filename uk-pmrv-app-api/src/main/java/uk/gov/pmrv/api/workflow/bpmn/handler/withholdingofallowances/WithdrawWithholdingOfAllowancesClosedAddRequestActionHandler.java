package uk.gov.pmrv.api.workflow.bpmn.handler.withholdingofallowances;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesClosedService;

@Service
@RequiredArgsConstructor
public class WithdrawWithholdingOfAllowancesClosedAddRequestActionHandler implements JavaDelegate {

    private final WithholdingOfAllowancesClosedService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.close((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
