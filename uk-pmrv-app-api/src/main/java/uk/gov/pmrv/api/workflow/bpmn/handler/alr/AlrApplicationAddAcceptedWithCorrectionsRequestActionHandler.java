package uk.gov.pmrv.api.workflow.bpmn.handler.alr;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAuthorityResponseService;

@Service
@RequiredArgsConstructor
public class AlrApplicationAddAcceptedWithCorrectionsRequestActionHandler implements JavaDelegate {

    private final ALRAuthorityResponseService alrAuthorityResponseService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        alrAuthorityResponseService.addSubmittedRequestAction(requestId, RequestActionType.ALR_APPLICATION_ACCEPTED_WITH_CORRECTIONS);
    }
}
