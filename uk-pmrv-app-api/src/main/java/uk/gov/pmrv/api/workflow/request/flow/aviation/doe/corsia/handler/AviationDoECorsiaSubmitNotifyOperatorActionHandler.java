package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AviationDoECorsiaSubmitNotifyOperatorActionHandler implements
    RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationDoECorsiaSubmitService submitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        submitService.applySubmitNotify(requestTask, payload.getDecisionNotification(), appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(), buildTaskVariables(requestTask));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR);
    }

    private Map<String, Object> buildTaskVariables(final RequestTask requestTask) {
        AviationDoECorsia doe = ((AviationDoECorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload()).getDoe();
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId());
        taskVariables.put(BpmnProcessConstants.AVIATION_DOE_CORSIA_SUBMIT_OUTCOME, AviationDoECorsiaSubmitOutcome.SUBMITTED);
        taskVariables.put(BpmnProcessConstants.AVIATION_DOE_CORSIA_IS_PAYMENT_REQUIRED, doe.getFee().isChargeOperator());
        if(doe.getFee().isChargeOperator()) {
            final Date paymentExpirationDate = DateUtils.atEndOfDay(doe.getFee().getFeeDetails().getDueDate());
            taskVariables.put(BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, paymentExpirationDate);
        }
        return taskVariables;
    }

}