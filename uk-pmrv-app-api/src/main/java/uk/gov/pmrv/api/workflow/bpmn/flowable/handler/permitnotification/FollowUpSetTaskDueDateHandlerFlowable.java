package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

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
public class FollowUpSetTaskDueDateHandlerFlowable implements JavaDelegate {

    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final LocalDate expirationDate = ((Date) execution
				.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE)).toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate();
        
        requestTaskTimeManagementService.setDueDateToTasks(requestId, RequestExpirationType.FOLLOW_UP_RESPONSE, expirationDate);
    }
}