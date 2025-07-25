package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.handler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.validation.PermitRevocationValidator;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@RequiredArgsConstructor
public class PermitRevocationNotifyOperatorActionHandler
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitRevocationValidator validator;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // validate
        final DecisionNotification permitDecisionNotification = taskActionPayload.getDecisionNotification();
        final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload =
            (PermitRevocationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        validator.validateNotifyUsers(requestTask, permitDecisionNotification, appUser);
        validator.validateSubmitRequestTaskPayload(taskPayload);

        final PermitRevocation permitRevocation = taskPayload.getPermitRevocation();

        // fill request payload
        final Request request = requestTask.getRequest();
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        requestPayload.setPermitRevocation(permitRevocation);
        requestPayload.setDecisionNotification(taskActionPayload.getDecisionNotification());

        //if no fee charge is requested, set request payment amount to zero
        if(BooleanUtils.isFalse(permitRevocation.getFeeCharged())) {
            requestPayload.setPaymentAmount(BigDecimal.ZERO);
        }

        //set request's submission date
        request.setSubmissionDate(LocalDateTime.now());
        
        // complete task
        workflowService.completeTask(requestTask.getProcessTaskId(), buildTaskVariables(permitRevocation));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION);
    }

    private Map<String, Object> buildTaskVariables(PermitRevocation permitRevocation) {
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put(BpmnProcessConstants.REVOCATION_OUTCOME, PermitRevocationOutcome.NOTIFY_OPERATOR);

        final LocalDate effectiveLocalDate = permitRevocation.getEffectiveDate();
		taskVariables.put(BpmnProcessConstants.REVOCATION_EFFECTIVE_DATE, DateUtils.atEndOfDay(effectiveLocalDate));
        
        final LocalDate reminderEffectiveLocalDate = effectiveLocalDate.minus(28, DAYS);
		taskVariables.put(BpmnProcessConstants.REVOCATION_REMINDER_EFFECTIVE_DATE,
				DateUtils.atEndOfDay(reminderEffectiveLocalDate));

        final LocalDate feeDate = permitRevocation.getFeeDate();
        taskVariables.put(BpmnProcessConstants.PAYMENT_EXPIRES, feeDate != null);
        final Date paymentExpirationDate = feeDate != null ? DateUtils.atEndOfDay(feeDate) : null;
        taskVariables.put(BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, paymentExpirationDate);
        
        return taskVariables;
    }
}
