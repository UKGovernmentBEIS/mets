package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@RequiredArgsConstructor
@Component
public class AviationVirSubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationVirApplyService applyService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final RequestTaskActionEmptyPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Submit VIR
        applyService.applySubmitAction(requestTask, appUser);

        // Set request's submission date
        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_VIR_SUBMIT_APPLICATION);
    }
}
