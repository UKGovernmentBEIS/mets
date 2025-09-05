package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRSaveAuthorityResponseTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAuthorityResponseService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ALRSaveAuthorityResponseActionHandler implements RequestTaskActionHandler<ALRSaveAuthorityResponseTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final ALRAuthorityResponseService alrAuthorityResponseService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        ALRSaveAuthorityResponseTaskActionPayload actionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        alrAuthorityResponseService.applyAuthorityResponseSaveAction(requestTask, actionPayload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.ALR_SAVE_AUTHORITY_RESPONSE);
    }
}
