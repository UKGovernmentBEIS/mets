package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2SubmitService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BDRS2ApplicationSaveActionHandler implements RequestTaskActionHandler<BDRS2ApplicationSaveRequestTaskActionPayload> {

    private final BDRS2SubmitService submitService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, BDRS2ApplicationSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        submitService.applySaveAction(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.BDRS2_SAVE_APPLICATION);
    }
}
