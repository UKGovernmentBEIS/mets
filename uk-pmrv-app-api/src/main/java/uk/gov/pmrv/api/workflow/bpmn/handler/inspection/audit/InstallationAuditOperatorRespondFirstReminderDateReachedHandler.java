package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.audit;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditExpirationDateService;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class InstallationAuditOperatorRespondFirstReminderDateReachedHandler
    implements JavaDelegate {

    private final InstallationAuditExpirationDateService installationAuditExpirationDateService;


    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate =
                (Date) execution.getVariable(BpmnProcessConstants.INSTALLATION_AUDIT_EXPIRATION_DATE);

        installationAuditExpirationDateService.sendRespondFirstReminderNotification(requestId, expirationDate);
    }
}
