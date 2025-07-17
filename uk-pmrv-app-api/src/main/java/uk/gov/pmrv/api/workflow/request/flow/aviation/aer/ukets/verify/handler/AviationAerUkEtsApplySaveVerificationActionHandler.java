package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.service.RequestAviationAerUkEtsApplyVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AviationAerUkEtsApplySaveVerificationActionHandler
    implements RequestTaskActionHandler<AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAviationAerUkEtsApplyVerificationService aerUkEtsApplyVerificationService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        AppUser appUser, AviationAerUkEtsSaveApplicationVerificationRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        aerUkEtsApplyVerificationService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION);
    }
}
