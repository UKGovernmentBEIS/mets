package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.withholdingofallowances;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesWithdrawnService;

@Service
@RequiredArgsConstructor
public class WithdrawnWithholdingOfAllowancesHandlerFlowable implements JavaDelegate {

    private final WithholdingOfAllowancesWithdrawnService service;

    @Override
    public void execute(final DelegateExecution execution) {
        service.withdraw((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
