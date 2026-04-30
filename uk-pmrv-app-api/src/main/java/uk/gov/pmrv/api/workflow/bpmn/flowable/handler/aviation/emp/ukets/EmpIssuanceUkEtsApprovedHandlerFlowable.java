package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.emp.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceUkEtsApprovedService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsApprovedHandlerFlowable implements JavaDelegate {

    private final EmpIssuanceUkEtsApprovedService empIssuanceUkEtsApprovedService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        empIssuanceUkEtsApprovedService.approveEmp(requestId);
    }
}
