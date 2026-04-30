package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.hseti;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETICompleteService;


@Service
@RequiredArgsConstructor
public class HsetiCompletedAddWithdrawnRequestActionHandlerFlowable implements JavaDelegate {

    private final HSETICompleteService completeService;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        completeService.addWithdrawnRequestAction(requestId);

    }
}
