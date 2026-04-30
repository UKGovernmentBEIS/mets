package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.vir;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationVirSendOfficialNoticeEmailHandlerFlowable implements JavaDelegate {

    private final AviationVirOfficialNoticeService virOfficialNoticeService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        virOfficialNoticeService.sendOfficialNotice(requestId);
    }
}
