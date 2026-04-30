package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permittransfer;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceDeemedWithdrawnAddRequestActionService;

@Service
@RequiredArgsConstructor
public class PermitTransferBDeemedWithdrawnAddRequestActionHandlerFlowable implements JavaDelegate {

    private final PermitIssuanceDeemedWithdrawnAddRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.addRequestAction(requestId);
    }
}
