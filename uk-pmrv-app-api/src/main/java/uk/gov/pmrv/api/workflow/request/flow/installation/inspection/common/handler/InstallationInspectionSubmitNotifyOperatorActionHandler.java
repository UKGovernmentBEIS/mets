package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.InstallationInspectionSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public abstract class InstallationInspectionSubmitNotifyOperatorActionHandler
        extends AbstractHandlerWithSubmitOutcomeProcessConstant
        implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final List<InstallationInspectionSubmitService> installationInspectionSubmitServices;
    private final WorkflowService workflowService;


    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();

        final InstallationInspectionApplicationSubmitRequestTaskPayload taskPayload =
                (InstallationInspectionApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        request.setSubmissionDate(LocalDateTime.now());

        final InstallationInspectionSubmitService installationInspectionSubmitService = installationInspectionSubmitServices.stream().filter(s -> s.getRequestType().equals(request.getType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        installationInspectionSubmitService.applySubmitNotify(requestTask, payload.getDecisionNotification(), appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID,
                requestTask.getRequest().getId(),

                this.getSubmitOutcomeBpmnConstantKey(),
                InstallationInspectionSubmitOutcome.SUBMITTED,

                BpmnProcessConstants.INSTALLATION_INSPECTION_ARE_FOLLOWUP_ACTIONS_REQUIRED,
                taskPayload.getInstallationInspection().getFollowUpActionsRequired().toString()
        ));

    }
}
