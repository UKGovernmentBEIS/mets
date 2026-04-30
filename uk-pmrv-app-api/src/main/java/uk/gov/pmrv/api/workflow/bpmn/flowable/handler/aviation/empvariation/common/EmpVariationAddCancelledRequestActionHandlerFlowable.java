package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.common;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.service.EmpVariationAddCancelledRequestActionService;

@Service
@RequiredArgsConstructor
public class EmpVariationAddCancelledRequestActionHandlerFlowable implements JavaDelegate {

	private final EmpVariationAddCancelledRequestActionService service;

    @Override
    public void execute(final DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String userRole = (String) execution.getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE);
        service.add(requestId, userRole);
    }
}
