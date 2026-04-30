package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationAddDeemedWithdrawnRequestActionService;

@Service
@RequiredArgsConstructor
public class PermitVariationAddDeemedWithdrawnRequestActionHandlerFlowable implements JavaDelegate {

	private final PermitVariationAddDeemedWithdrawnRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) {
    	service.add((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
