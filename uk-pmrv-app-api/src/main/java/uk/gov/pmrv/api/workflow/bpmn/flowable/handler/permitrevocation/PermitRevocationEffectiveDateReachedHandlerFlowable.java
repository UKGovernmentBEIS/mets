package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitrevocation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.PermitRevokedService;

@Service
@RequiredArgsConstructor
public class PermitRevocationEffectiveDateReachedHandlerFlowable implements JavaDelegate {

    private final PermitRevokedService service;

    @Override
    public void execute(final DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

        service.executePermitRevokedPostActions(requestId);

        // Add variables for triggering AER and ALR and payment if required
        service.constructAerAndAlrVariables(requestId).forEach(execution::setVariable);
    }
}
