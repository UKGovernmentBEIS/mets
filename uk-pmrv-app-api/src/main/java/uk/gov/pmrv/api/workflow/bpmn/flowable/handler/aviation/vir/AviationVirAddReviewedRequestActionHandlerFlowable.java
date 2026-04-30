package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.vir;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationVirAddReviewedRequestActionHandlerFlowable implements JavaDelegate {

    private final AviationVirReviewService virReviewService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        virReviewService.addReviewedRequestAction(requestId);
    }
}
