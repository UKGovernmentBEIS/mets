package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitNotificationGenerateOfficialNoticeHandlerFlowable implements JavaDelegate {

    private final PermitNotificationOfficialNoticeService service;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final DeterminationType determinationType =
                (DeterminationType) execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);

        switch (determinationType) {
            case GRANTED ->
                    service.generateAndSaveGrantedOfficialNotice(requestId);
            case REJECTED ->
                    service.generateAndSaveRejectedOfficialNotice(requestId);
            case COMPLETED ->
                    service.generateAndSaveCompletedOfficialNotice(requestId);
            default ->
                    throw new UnsupportedOperationException(
                            "Determination type is not supported: " + determinationType
                    );
        }
    }
}
