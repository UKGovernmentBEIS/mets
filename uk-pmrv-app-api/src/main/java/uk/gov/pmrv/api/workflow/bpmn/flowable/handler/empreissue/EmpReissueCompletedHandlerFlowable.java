package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.empreissue;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.bpmn.flowable.FlowableWorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCompletedService;

@Service
@RequiredArgsConstructor
public class EmpReissueCompletedHandlerFlowable implements JavaDelegate {

    private final FlowableWorkflowService workflowService;
    private final RequestQueryService requestQueryService;
    private final ReissueCompletedService reissueCompletedService;

    @Override
    public void execute(DelegateExecution execution) {
        final String batchRequestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BATCH_REQUEST_BUSINESS_KEY);
        final String batchProcessInstanceId = workflowService.getProcessInstanceIdByBusinessKey(batchRequestBusinessKey);
        final Request batchRequest = requestQueryService.findByProcessInstanceId(batchProcessInstanceId);
        final String batchRequestId = batchRequest.getId();

        final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final boolean reissueSucceeded = (Boolean) execution.getVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED);

        reissueCompletedService.reissueCompleted(batchRequestId, accountId, reissueSucceeded, true);
    }
}
