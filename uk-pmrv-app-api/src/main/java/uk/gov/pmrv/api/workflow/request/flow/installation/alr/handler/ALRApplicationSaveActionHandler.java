package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRSubmitService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ALRApplicationSaveActionHandler implements RequestTaskActionHandler<ALRApplicationSaveRequestTaskActionPayload> {

    private final ALRSubmitService submitService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, ALRApplicationSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        submitService.applySaveAction(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.ALR_SAVE_APPLICATION);
    }
}
