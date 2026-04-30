package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.rfi;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiCancelledService;

@Service
@RequiredArgsConstructor
public class RfiCancelledHandlerFlowable implements JavaDelegate {

    private final RfiCancelledService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.cancel((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
