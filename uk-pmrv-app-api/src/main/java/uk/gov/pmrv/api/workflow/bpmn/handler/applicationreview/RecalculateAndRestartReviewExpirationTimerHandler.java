package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RecalculateDueDateAfterRfiService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecalculateAndRestartReviewExpirationTimerHandler implements JavaDelegate {

	private final RequestTaskTimeManagementService requestTaskTimeManagementService;
    private final RecalculateDueDateAfterRfiService recalculateDueDateAfterRfiService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date rfiStartedDate = (Date) execution.getVariable(BpmnProcessConstants.RFI_START_TIME);
        final Date reviewExpirationDueDate = (Date) execution.getVariable(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);
        
		final LocalDateTime newReviewExpirationDueDateLocal = recalculateDueDateAfterRfiService
				.recalculateDueDate(rfiStartedDate, reviewExpirationDueDate);
		
		requestTaskTimeManagementService.unpauseTasksAndUpdateDueDate(requestId, RequestExpirationType.APPLICATION_REVIEW,
				newReviewExpirationDueDateLocal.toLocalDate());
        
		Map<String, Object> expirationVars = requestExpirationVarsBuilder.buildExpirationVars(
				RequestExpirationType.APPLICATION_REVIEW,
				Date.from(newReviewExpirationDueDateLocal.atZone(ZoneId.systemDefault()).toInstant()));
        execution.setVariables(expirationVars);
    }
}
