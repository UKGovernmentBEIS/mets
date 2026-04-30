package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.doal;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalCancelService;

@Service
@RequiredArgsConstructor
public class DoalApplicationCancelledHandlerFlowable implements JavaDelegate {

    private final DoalCancelService doalCancelService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        doalCancelService.cancel(requestId);
    }
}
