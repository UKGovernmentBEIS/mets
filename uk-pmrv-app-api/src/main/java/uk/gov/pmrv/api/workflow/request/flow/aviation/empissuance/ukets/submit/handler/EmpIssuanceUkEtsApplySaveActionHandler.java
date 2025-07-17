package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.service.RequestEmpUkEtsService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EmpIssuanceUkEtsApplySaveActionHandler implements RequestTaskActionHandler<EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload> {

    private final RequestEmpUkEtsService requestEmpUkEtsService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestEmpUkEtsService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION);
    }
}
