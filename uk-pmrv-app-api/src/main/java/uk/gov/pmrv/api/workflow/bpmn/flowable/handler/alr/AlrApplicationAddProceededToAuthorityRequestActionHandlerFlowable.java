package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRSubmitService;

@Service
@RequiredArgsConstructor
public class AlrApplicationAddProceededToAuthorityRequestActionHandlerFlowable implements JavaDelegate {

    private final ALRSubmitService alrSubmitService;

    @Override
    public void execute(final DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        alrSubmitService.addProceededToAuthorityRequestAction(requestId);
    }
}
