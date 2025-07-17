package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationUpdatePermitService;

@Service
@RequiredArgsConstructor
public class PermitVariationUpdatePermitUponGrantedDeterminationHandler implements JavaDelegate {

	private final PermitVariationUpdatePermitService service;

    @Override
    public void execute(DelegateExecution execution) {
    	service.updatePermitUponGrantedDetermination((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }

}
