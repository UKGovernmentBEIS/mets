package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationAddCancelledRequestActionService;

@Service
@RequiredArgsConstructor
public class PermitVariationAddCancelledRequestActionHandlerFlowable implements JavaDelegate {

    private final PermitVariationAddCancelledRequestActionService service;

    @Override
    public void execute(final DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String userRole = (String) execution.getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE);
        service.add(requestId, userRole);
    }
}
