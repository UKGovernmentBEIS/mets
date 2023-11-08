package uk.gov.pmrv.api.workflow.bpmn.handler.permitissuance;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceRejectedGenerateOfficialNoticeHandler implements JavaDelegate {

    private final PermitIssuanceOfficialNoticeService service;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.generateAndSaveRejectedOfficialNotice(requestId);
    }
}
