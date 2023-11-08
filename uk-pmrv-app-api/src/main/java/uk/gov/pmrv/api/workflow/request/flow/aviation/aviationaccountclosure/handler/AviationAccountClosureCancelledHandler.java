package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service.RequestAviationAccountClosureService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@Component
@RequiredArgsConstructor
public class AviationAccountClosureCancelledHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

	private final RequestAviationAccountClosureService requestAviationAccountClosureService;
    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;
    

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final RequestTaskActionEmptyPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestAviationAccountClosureService.cancel(requestTask.getRequest().getId());
        
        workflowService.completeTask(requestTask.getProcessTaskId());
    }

	@Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_CANCEL_APPLICATION);
    }
}
