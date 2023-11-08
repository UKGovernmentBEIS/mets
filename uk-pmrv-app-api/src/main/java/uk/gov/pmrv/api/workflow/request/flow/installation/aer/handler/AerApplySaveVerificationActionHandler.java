package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.RequestAerApplyVerificationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AerApplySaveVerificationActionHandler implements RequestTaskActionHandler<AerSaveApplicationVerificationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAerApplyVerificationService requestAerApplyVerificationService;

    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        PmrvUser pmrvUser,
                        AerSaveApplicationVerificationRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestAerApplyVerificationService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AER_SAVE_APPLICATION_VERIFICATION);
    }
}
