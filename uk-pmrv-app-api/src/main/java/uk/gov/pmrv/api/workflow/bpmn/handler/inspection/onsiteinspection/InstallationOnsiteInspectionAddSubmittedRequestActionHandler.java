package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.onsiteinspection;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionAddSubmittedRequestActionService;

@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionAddSubmittedRequestActionHandler implements JavaDelegate {

    private final InstallationOnsiteInspectionAddSubmittedRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.add(requestId);
    }
}
