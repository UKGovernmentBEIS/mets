package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation.DoalProceedToAuthorityValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DoalSubmitApplicationProceedToAuthorityActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final DoalProceedToAuthorityValidator doalProceedToAuthorityValidator;
    private final DoalSubmitService doalSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser, RequestTaskActionEmptyPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate
        doalProceedToAuthorityValidator.validateComplete(requestTask);

        // Complete
        doalSubmitService.complete(requestTask);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.DOAL_SUBMIT_OUTCOME, DoalSubmitOutcome.SUBMITTED,
                        BpmnProcessConstants.DOAL_DETERMINATION, DoalDeterminationType.PROCEED_TO_AUTHORITY,
                        BpmnProcessConstants.DOAL_SEND_NOTICE, false)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.DOAL_PROCEED_TO_AUTHORITY_APPLICATION);
    }
}
