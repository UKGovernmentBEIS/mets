package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.onsiteinspection;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionExpirationDateService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionOperatorRespondSecondReminderDateReachedHandler
    implements JavaDelegate {

    private final InstallationOnsiteInspectionExpirationDateService installationOnsiteInspectionExpirationDateService;


    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate =
                (Date) execution.getVariable(BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_EXPIRATION_DATE);

        installationOnsiteInspectionExpirationDateService
                .sendRespondSecondReminderNotification(requestId, expirationDate);
    }
}
