package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.service.RequestAviationDreUkEtsApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@Component
@RequiredArgsConstructor
public class AviationDreUkEtsSubmitNotifyOperatorActionHandler implements
    RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {
    private final RequestTaskService requestTaskService;
    private final RequestAviationDreUkEtsApplyService aviationDreApplyService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        aviationDreApplyService.applySubmitNotify(requestTask, payload.getDecisionNotification(), pmrvUser);

        workflowService.completeTask(requestTask.getProcessTaskId(), buildTaskVariables(requestTask));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR);
    }

    private Map<String, Object> buildTaskVariables(final RequestTask requestTask) {
        AviationDre dre = ((AviationDreUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload()).getDre();
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId());
        taskVariables.put(BpmnProcessConstants.DRE_SUBMIT_OUTCOME, AviationDreSubmitOutcome.SUBMITTED);
        taskVariables.put(BpmnProcessConstants.DRE_IS_PAYMENT_REQUIRED, dre.getFee().isChargeOperator());
        if(dre.getFee().isChargeOperator()) {
            final Date paymentExpirationDate = DateUtils.convertLocalDateToDate(dre.getFee().getFeeDetails().getDueDate());
            taskVariables.put(BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, paymentExpirationDate);
        }
        return taskVariables;
    }

}