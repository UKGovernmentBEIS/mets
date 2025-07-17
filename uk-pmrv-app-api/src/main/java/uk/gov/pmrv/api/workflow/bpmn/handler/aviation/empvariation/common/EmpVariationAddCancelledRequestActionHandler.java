package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.empvariation.common;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.service.EmpVariationAddCancelledRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationAddCancelledRequestActionHandler implements JavaDelegate {

	private final EmpVariationAddCancelledRequestActionService service;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String userRole = (String) execution.getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE);
        service.add(requestId, userRole);
    }
}
