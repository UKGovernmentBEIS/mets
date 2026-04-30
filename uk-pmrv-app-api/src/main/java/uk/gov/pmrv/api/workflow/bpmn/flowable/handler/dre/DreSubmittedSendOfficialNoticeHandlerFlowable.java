package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.dre;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreOfficialNoticeSendService;

@Service
@RequiredArgsConstructor
public class DreSubmittedSendOfficialNoticeHandlerFlowable implements JavaDelegate {
	
	private final DreOfficialNoticeSendService dreOfficialNoticeSendService;
	
	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		dreOfficialNoticeSendService.sendOfficialNotice(requestId);
	}

}