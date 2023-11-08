package uk.gov.pmrv.api.workflow.bpmn.handler.dre;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreOfficialNoticeGenerateService;

@Service
@RequiredArgsConstructor
public class DreSubmittedGenerateOfficialNoticeHandler implements JavaDelegate {
	
	private final DreOfficialNoticeGenerateService dreOfficialNoticeGenerateService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		dreOfficialNoticeGenerateService.generateDreSubmittedOfficialNotice(requestId);
	}

}
