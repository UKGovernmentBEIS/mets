package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.emp.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsDeemedWithdrawnGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final EmpIssuanceOfficialNoticeService empIssuanceOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        empIssuanceOfficialNoticeService.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);
    }
}
