package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalSubmitService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DoalSaveApplicationActionHandler implements RequestTaskActionHandler<DoalSaveApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final DoalSubmitService doalSubmitService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        DoalSaveApplicationRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        doalSubmitService.applySaveAction(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.DOAL_SAVE_APPLICATION);
    }
}
