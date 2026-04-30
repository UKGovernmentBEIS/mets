package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirDeadlineService;

@Service
@RequiredArgsConstructor
public class AirDeadlineDateReachedHandlerFlowable implements JavaDelegate {

    private final AirDeadlineService deadlineService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        deadlineService.sendDeadlineNotification(requestId);
    }
}
