package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.vir;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirReviewService;

@Service
@RequiredArgsConstructor
public class VirAddReviewedRequestActionHandlerFlowable implements JavaDelegate {

    private final VirReviewService virReviewService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);

        virReviewService.addReviewedRequestAction(requestId);
    }
}
