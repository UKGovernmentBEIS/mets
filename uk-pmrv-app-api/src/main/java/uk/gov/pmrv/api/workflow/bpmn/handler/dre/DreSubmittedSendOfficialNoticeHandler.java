package uk.gov.pmrv.api.workflow.bpmn.handler.dre;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreOfficialNoticeSendService;

@Service
@RequiredArgsConstructor
public class DreSubmittedSendOfficialNoticeHandler implements JavaDelegate {
	
	private final DreOfficialNoticeSendService dreOfficialNoticeSendService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		dreOfficialNoticeSendService.sendOfficialNotice(requestId);
	}

}