package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.service.RequestAviationAerCorsiaApplyVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AviationAerCorsiaApplySaveVerificationActionHandler
    implements RequestTaskActionHandler<AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAviationAerCorsiaApplyVerificationService requestAviationAerCorsiaApplyVerificationService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        AppUser appUser, AviationAerCorsiaSaveApplicationVerificationRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestAviationAerCorsiaApplyVerificationService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION);
    }
}
