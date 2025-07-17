package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReturnedToOperatorRequestActionPayload;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ALRApplicationVerificationReturnToOperatorActionHandler implements RequestTaskActionHandler<ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, ALRApplicationVerificationReturnToOperatorRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();

        ALRVerificationReturnedToOperatorRequestActionPayload actionPayload = ALRVerificationReturnedToOperatorRequestActionPayload
                .builder()
                .changesRequired(payload.getChangesRequired())
                .payloadType(RequestActionPayloadType.ALR_VERIFICATION_RETURNED_TO_OPERATOR_PAYLOAD)
                .build();

        requestService.addActionToRequest(request,
                actionPayload,
                RequestActionType.ALR_VERIFICATION_RETURNED_TO_OPERATOR,
                appUser.getUserId());

        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.ALR_VERIFICATION_RETURN_TO_OPERATOR);
    }
}
