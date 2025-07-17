package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerSubmitOutcome;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NerCancelApplicationActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final RequestTaskActionEmptyPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.NER_SUBMIT_OUTCOME, NerSubmitOutcome.CANCELLED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_CANCEL_APPLICATION);
    }
}
