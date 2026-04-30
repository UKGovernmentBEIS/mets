package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitVariationDeemedWithdrawnGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final PermitVariationOfficialNoticeService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.generateAndSaveDeemedWithdrawnOfficialNotice((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
