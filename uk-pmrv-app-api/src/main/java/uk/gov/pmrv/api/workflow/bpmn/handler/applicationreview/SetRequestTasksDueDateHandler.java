package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

@Service
@RequiredArgsConstructor
public class SetRequestTasksDueDateHandler implements JavaDelegate {

    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final LocalDate expirationDate = ((Date) execution
				.getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE)).toInstant()
				.atZone(ZoneId.systemDefault()).toLocalDate();
        requestTaskTimeManagementService
            .setDueDateToTasks(requestId, RequestExpirationType.APPLICATION_REVIEW, expirationDate);
    }
}
