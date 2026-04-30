package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2CompleteService;

@Service
@RequiredArgsConstructor
public class Bdrs2CompletedAddRequestActionHandlerFlowable implements JavaDelegate {

    private final BDRS2CompleteService bdrs2CompleteService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        bdrs2CompleteService.addRequestAction(requestId);
    }
}
