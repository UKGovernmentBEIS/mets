package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.dre;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DrePopulateRequestMetadataWithReportableEmissionsService;

@Service
@RequiredArgsConstructor
public class DrePopulateRequestMetadataWithReportableEmissionsHandlerFlowable implements JavaDelegate {

	private final DrePopulateRequestMetadataWithReportableEmissionsService service;
	
	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		service.updateRequestMetadata(requestId);
	}

}
