package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.inspection.onsiteinspection;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionSetOperatorResponseTaskDueDateHandlerFlowable implements JavaDelegate {

    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Override
    public void execute(DelegateExecution execution)  {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final LocalDate expirationDate = ((Date) execution
            .getVariable(BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_EXPIRATION_DATE)).toInstant()
            .atZone(ZoneId.systemDefault()).toLocalDate();

        requestTaskTimeManagementService
                .setDueDateToTasks(requestId, RequestExpirationType.INSTALLATION_ONSITE_INSPECTION, expirationDate);
    }

}
