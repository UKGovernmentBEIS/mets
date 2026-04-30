package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.inspection.onsiteinspection;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionExpirationDateService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionOperatorRespondSecondReminderDateReachedHandlerFlowable
    implements JavaDelegate {

    private final InstallationOnsiteInspectionExpirationDateService installationOnsiteInspectionExpirationDateService;


    @Override
    public void execute(DelegateExecution execution)  {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date expirationDate =
                (Date) execution.getVariable(BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_EXPIRATION_DATE);

        installationOnsiteInspectionExpirationDateService
                .sendRespondSecondReminderNotification(requestId, expirationDate);
    }
}
