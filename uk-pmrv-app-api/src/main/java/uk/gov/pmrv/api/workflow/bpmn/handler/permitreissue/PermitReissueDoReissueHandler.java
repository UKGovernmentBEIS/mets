package uk.gov.pmrv.api.workflow.bpmn.handler.permitreissue;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service.PermitReissueDoReissueService;

@Log4j2
@Service
@RequiredArgsConstructor
public class PermitReissueDoReissueHandler implements JavaDelegate {
	
	private final PermitReissueDoReissueService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		try {
			service.doReissue(requestId);
		} catch (Exception e) {
			log.error(String.format("PermitReissueDoReissueHandler error for requestId %s", requestId), e);
			throw new BpmnError("PermitReissueDoReissueHandler", e);
		}
	}

}