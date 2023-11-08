package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.vir;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationVirAddReviewedRequestActionHandler implements JavaDelegate {

    private final AviationVirReviewService virReviewService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        
        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        virReviewService.addReviewedRequestAction(requestId);
    }
}
