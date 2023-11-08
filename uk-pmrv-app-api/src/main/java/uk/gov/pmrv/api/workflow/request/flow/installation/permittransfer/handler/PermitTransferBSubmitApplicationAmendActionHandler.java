package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceReviewService;

@Service
@RequiredArgsConstructor
public class PermitTransferBSubmitApplicationAmendActionHandler implements
    RequestTaskActionHandler<PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitIssuanceReviewService permitIssuanceReviewService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final PmrvUser pmrvUser,
                        final PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        permitIssuanceReviewService.submitAmendedPermit(payload, requestTask);

        requestService.addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMITTED,
            pmrvUser.getUserId());

        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_TRANSFER_B_SUBMIT_APPLICATION_AMEND);
    }
}
