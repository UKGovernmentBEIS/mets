package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.emp.corsia;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.EmpIssuanceCorsiaDeemedWithdrawnAddRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaDeemedWithdrawnAddRequestActionHandler implements JavaDelegate {

    private final EmpIssuanceCorsiaDeemedWithdrawnAddRequestActionService addRequestActionService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        addRequestActionService.addRequestAction(requestId);
    }
}
