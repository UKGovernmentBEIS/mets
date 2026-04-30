package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitrevocation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.PermitRevocationCancelledService;

@Service
@RequiredArgsConstructor
public class PermitRevocationCancelledHandlerFlowable implements JavaDelegate {

    private final PermitRevocationCancelledService service;

    @Override
    public void execute(final DelegateExecution execution) {
        service.cancel((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
