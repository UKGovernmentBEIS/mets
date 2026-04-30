package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitsurrender;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final PermitSurrenderOfficialNoticeService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final PermitSurrenderReviewDeterminationType determinationType = (PermitSurrenderReviewDeterminationType) execution
                .getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);
        switch (determinationType) {
        case GRANTED:
            service.generateAndSaveGrantedOfficialNotice(requestId);    
            break;
        case REJECTED:
            service.generateAndSaveRejectedOfficialNotice(requestId);    
            break;
        case DEEMED_WITHDRAWN:
            service.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);    
            break;
        default:
            throw new UnsupportedOperationException("Determination type is not supported: " + determinationType);
        }
        
    }
}
