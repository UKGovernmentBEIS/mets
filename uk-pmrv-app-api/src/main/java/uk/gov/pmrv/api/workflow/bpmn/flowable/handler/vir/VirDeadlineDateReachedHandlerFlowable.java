package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.vir;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirDeadlineService;

@Service
@RequiredArgsConstructor
public class VirDeadlineDateReachedHandlerFlowable implements JavaDelegate {

    private final VirDeadlineService virDeadlineService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        virDeadlineService.sendDeadlineNotification(requestId);
    }
}
