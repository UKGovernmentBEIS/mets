package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationSubmittedService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.validation.PermanentCessationSubmissionValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermanentCessationNotifyOperatorHandler
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermanentCessationService permanentCessationService;
    private final PermanentCessationSubmissionValidator permanentCessationSubmissionValidator;
    private final PermanentCessationSubmittedService permanentCessationSubmittedService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        //validate
        permanentCessationSubmissionValidator.validate(requestTask, payload, appUser);

        // save
        permanentCessationService.savePermanentCessationDecisionNotification(payload, requestTask);

        // complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.PERMANENT_CESSATION_SUBMIT_OUTCOME,
                    PermanentCessationSubmitOutcome.SUBMITTED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION);
    }

}
