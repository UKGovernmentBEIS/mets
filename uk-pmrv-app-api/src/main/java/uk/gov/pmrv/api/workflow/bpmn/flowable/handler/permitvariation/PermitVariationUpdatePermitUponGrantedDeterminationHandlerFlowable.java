package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationUpdatePermitService;

@Service
@RequiredArgsConstructor
public class PermitVariationUpdatePermitUponGrantedDeterminationHandlerFlowable implements JavaDelegate {

	private final PermitVariationUpdatePermitService service;

    @Override
    public void execute(DelegateExecution execution) {
    	service.updatePermitUponGrantedDetermination((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
