package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.service.PermitVariationAmendService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermitVariationSubmitApplicationAmendActionHandler implements
    RequestTaskActionHandler<PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitVariationAmendService permitVariationAmendService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        permitVariationAmendService.submitAmendedPermitVariation(payload, requestTask);

        requestService.addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMITTED,
            pmrvUser.getUserId());

        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND);
    }
}
