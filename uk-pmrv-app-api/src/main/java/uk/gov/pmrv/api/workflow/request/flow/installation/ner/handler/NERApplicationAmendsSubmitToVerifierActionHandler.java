package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AmendsOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERAmendsSubmitService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class NERApplicationAmendsSubmitToVerifierActionHandler implements
        RequestTaskActionHandler<NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NERAmendsSubmitService amendsSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, NERApplicationAmendsSubmitToVerifierRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        amendsSubmitService.sendAmendsToVerifier(payload, requestTask, appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.AMENDS_OUTCOME, AmendsOutcome.SUBMITTED_TO_VERIFIER,
                        BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.NER_AMEND.getCode())
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER);
    }
}
