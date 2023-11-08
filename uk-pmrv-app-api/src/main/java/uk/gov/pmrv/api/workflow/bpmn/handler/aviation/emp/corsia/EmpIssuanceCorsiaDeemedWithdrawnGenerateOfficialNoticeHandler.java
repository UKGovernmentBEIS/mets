package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.emp.corsia;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.EmpIssuanceCorsiaOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaDeemedWithdrawnGenerateOfficialNoticeHandler implements JavaDelegate {

    private final EmpIssuanceCorsiaOfficialNoticeService empIssuanceOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        empIssuanceOfficialNoticeService.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);
    }
}
