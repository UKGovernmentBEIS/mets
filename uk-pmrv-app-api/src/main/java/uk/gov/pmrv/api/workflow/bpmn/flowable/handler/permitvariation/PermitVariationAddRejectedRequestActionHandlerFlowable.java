package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationAddRejectedRequestActionService;

@Service
@RequiredArgsConstructor
public class PermitVariationAddRejectedRequestActionHandlerFlowable implements JavaDelegate {

	private final PermitVariationAddRejectedRequestActionService service;

    @Override
    public void execute(DelegateExecution execution) {
    	service.add((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
