package uk.gov.pmrv.api.workflow.bpmn.handler.withholdingofallowances;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesApplicationCancelledService;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesAddCancelledRequestActionHandler implements JavaDelegate {

    private final WithholdingOfAllowancesApplicationCancelledService service;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        service.cancel((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
