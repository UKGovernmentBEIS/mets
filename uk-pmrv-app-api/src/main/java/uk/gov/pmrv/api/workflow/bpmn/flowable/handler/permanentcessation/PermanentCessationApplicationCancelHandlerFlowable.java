package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permanentcessation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationService;

@Service
@RequiredArgsConstructor
public class PermanentCessationApplicationCancelHandlerFlowable implements JavaDelegate {

    private final PermanentCessationService permanentCessationService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        permanentCessationService.cancel(requestId);
    }
}
