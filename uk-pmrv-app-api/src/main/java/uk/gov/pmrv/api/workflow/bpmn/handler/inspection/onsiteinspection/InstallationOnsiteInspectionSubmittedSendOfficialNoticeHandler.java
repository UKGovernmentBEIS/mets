package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.onsiteinspection;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionOfficialNoticeService;

@Service
@RequiredArgsConstructor

public class InstallationOnsiteInspectionSubmittedSendOfficialNoticeHandler implements JavaDelegate {

    private final InstallationOnsiteInspectionOfficialNoticeService installationOnsiteInspectionOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        installationOnsiteInspectionOfficialNoticeService.sendOfficialNotice(requestId);
    }

}
