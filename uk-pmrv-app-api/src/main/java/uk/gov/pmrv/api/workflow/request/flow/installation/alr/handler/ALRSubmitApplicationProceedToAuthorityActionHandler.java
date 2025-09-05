package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRProceedToAuthorityValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ALRSubmitApplicationProceedToAuthorityActionHandler implements
        RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final ALRProceedToAuthorityValidator alrProceedToAuthorityValidator;
    private final ALRSubmitService alrSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        ALRApplicationRegulatorReviewSubmitRequestTaskPayload requestTaskPayload = (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        DoalProceedToAuthorityDetermination determination = (DoalProceedToAuthorityDetermination) requestTaskPayload.getRegulatorReviewOutcome().getDetermination();
        boolean needsOfficialNotice = Boolean.TRUE.equals(determination.getNeedsOfficialNotice());

        if (needsOfficialNotice) {
            alrProceedToAuthorityValidator.validateNotify(requestTask, taskActionPayload.getDecisionNotification(), appUser);
            alrSubmitService.notifyOperator(requestTask, taskActionPayload);
        }
        else {
            alrProceedToAuthorityValidator.validateComplete(requestTask);
            alrSubmitService.complete(requestTask);
        }



        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.ALR_REGULATOR_REVIEW_OUTCOME, ALROutcome.SUBMITTED.name(),
                        BpmnProcessConstants.ALR_DETERMINATION, DoalDeterminationType.PROCEED_TO_AUTHORITY,
                        BpmnProcessConstants.ALR_SEND_NOTICE, needsOfficialNotice)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.ALR_PROCEED_TO_AUTHORITY);
    }
}
