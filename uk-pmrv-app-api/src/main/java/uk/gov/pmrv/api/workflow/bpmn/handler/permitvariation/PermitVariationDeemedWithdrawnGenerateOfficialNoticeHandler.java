package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitVariationDeemedWithdrawnGenerateOfficialNoticeHandler implements JavaDelegate {

    private final PermitVariationOfficialNoticeService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.generateAndSaveDeemedWithdrawnOfficialNotice((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
