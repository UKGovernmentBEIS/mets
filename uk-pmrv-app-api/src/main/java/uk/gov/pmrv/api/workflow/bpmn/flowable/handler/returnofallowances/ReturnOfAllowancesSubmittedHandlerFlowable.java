package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.returnofallowances;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesSubmittedService;

@Service
@RequiredArgsConstructor
public class ReturnOfAllowancesSubmittedHandlerFlowable implements JavaDelegate {

    private final ReturnOfAllowancesSubmittedService service;

    @Override
    public void execute(final DelegateExecution execution) {
        service.submit((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
