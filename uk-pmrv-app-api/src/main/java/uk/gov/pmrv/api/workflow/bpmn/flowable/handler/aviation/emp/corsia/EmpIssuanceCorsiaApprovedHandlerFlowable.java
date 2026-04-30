package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.emp.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.EmpIssuanceCorsiaApprovedService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaApprovedHandlerFlowable implements JavaDelegate {

    private final EmpIssuanceCorsiaApprovedService empIssuanceCorsiaApprovedService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        empIssuanceCorsiaApprovedService.approveEmp(requestId);
    }
}
