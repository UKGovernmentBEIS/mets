package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service.RequestAviationAccountClosureService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@RequiredArgsConstructor
@Component
public class AviationAccountClosureSaveActionHandler implements RequestTaskActionHandler<AviationAccountClosureSaveRequestTaskActionPayload> {

    private final RequestAviationAccountClosureService requestAviationAccountClosureService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        AviationAccountClosureSaveRequestTaskActionPayload actionPayload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestAviationAccountClosureService.applySavePayload(actionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION);
    }
}
