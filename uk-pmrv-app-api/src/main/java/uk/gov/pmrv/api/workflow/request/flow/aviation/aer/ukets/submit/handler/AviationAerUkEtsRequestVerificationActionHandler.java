package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service.RequestAviationAerUkEtsSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AviationAerUkEtsRequestVerificationActionHandler
    implements RequestTaskActionHandler<AviationAerApplicationRequestVerificationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAviationAerUkEtsSubmitService requestAviationAerUkEtsSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        AviationAerApplicationRequestVerificationRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        requestAviationAerUkEtsSubmitService.sendToVerifier(payload, requestTask, appUser);

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AVIATION_AER_OUTCOME, AviationAerOutcome.VERIFICATION_REQUESTED));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_VERIFICATION);
    }
}
