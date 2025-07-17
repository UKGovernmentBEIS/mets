package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.audit;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class InstallationAuditSubmittedGenerateOfficialNoticeHandler implements JavaDelegate {

    private final InstallationAuditOfficialNoticeService installationAuditOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        installationAuditOfficialNoticeService.generateInstallationAuditSubmittedOfficialNotice(requestId);
    }
}
