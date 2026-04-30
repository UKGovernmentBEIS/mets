package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsOperatorLedApprovedGenerateDocumentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsApprovedGenerateDocumentsHandlerFlowable implements JavaDelegate {

	private final EmpVariationUkEtsOperatorLedApprovedGenerateDocumentsService service;

	@Override
	public void execute(DelegateExecution execution) {
		service.generateDocuments((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
