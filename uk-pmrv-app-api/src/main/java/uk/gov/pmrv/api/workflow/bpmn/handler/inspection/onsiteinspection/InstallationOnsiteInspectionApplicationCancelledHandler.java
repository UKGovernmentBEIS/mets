package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.onsiteinspection;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionSubmitService;

@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionApplicationCancelledHandler implements JavaDelegate {
    private final InstallationOnsiteInspectionSubmitService installationOnsiteInspectionSubmitService;


    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        installationOnsiteInspectionSubmitService.cancel(requestId);
    }
}
