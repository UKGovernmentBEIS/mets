package uk.gov.pmrv.api.workflow.bpmn.handler.doal;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalAuthorityResponseService;

@Service
@RequiredArgsConstructor
public class DoalApplicationAddAcceptedRequestActionHandler implements JavaDelegate {

    private final DoalAuthorityResponseService doalAuthorityResponseService;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        doalAuthorityResponseService.addSubmittedRequestAction(requestId, RequestActionType.DOAL_APPLICATION_ACCEPTED);
    }
}
