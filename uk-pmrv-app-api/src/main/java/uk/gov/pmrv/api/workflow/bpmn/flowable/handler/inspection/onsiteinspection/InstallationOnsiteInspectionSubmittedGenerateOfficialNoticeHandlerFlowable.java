package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.inspection.onsiteinspection;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionSubmittedGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final InstallationOnsiteInspectionOfficialNoticeService installationOnsiteInspectionOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        installationOnsiteInspectionOfficialNoticeService
                .generateInstallationOnsiteInspectionSubmittedOfficialNotice(requestId);
    }
}
