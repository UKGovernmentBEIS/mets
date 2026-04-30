package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.emp.corsia;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.EmpIssuanceCorsiaOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaDeemedWithdrawnGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final EmpIssuanceCorsiaOfficialNoticeService empIssuanceOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        empIssuanceOfficialNoticeService.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);
    }
}
