package uk.gov.pmrv.api.workflow.bpmn.handler.vir;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirDeadlineService;

@Service
@RequiredArgsConstructor
public class VirDeadlineDateReachedHandler implements JavaDelegate {

    private final VirDeadlineService virDeadlineService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        virDeadlineService.sendDeadlineNotification(requestId);
    }
}
