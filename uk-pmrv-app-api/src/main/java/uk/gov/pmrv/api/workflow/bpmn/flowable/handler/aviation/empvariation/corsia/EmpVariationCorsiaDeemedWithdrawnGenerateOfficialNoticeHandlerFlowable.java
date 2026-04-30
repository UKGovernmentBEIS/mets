package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service.EmpVariationCorsiaOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaDeemedWithdrawnGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

	private final EmpVariationCorsiaOfficialNoticeService service;

	@Override
	public void execute(DelegateExecution execution) {
		service.generateAndSaveDeemedWithdrawnOfficialNotice((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
	}
}
