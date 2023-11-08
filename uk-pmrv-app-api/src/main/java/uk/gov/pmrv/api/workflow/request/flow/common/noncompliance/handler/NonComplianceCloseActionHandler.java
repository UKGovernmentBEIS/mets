package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseApplicationRequestTaskActionPayload;

@Component
@RequiredArgsConstructor
public class NonComplianceCloseActionHandler
    implements RequestTaskActionHandler<NonComplianceCloseApplicationRequestTaskActionPayload> {
    
    private final RequestTaskService requestTaskService;
    private final NonComplianceApplyService applyService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final NonComplianceCloseApplicationRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        applyService.applyCloseAction(requestTask, taskActionPayload);
        workflowService.completeTask(
            requestTask.getProcessTaskId(), 
            Map.of(BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.CLOSED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION);
    }
}
