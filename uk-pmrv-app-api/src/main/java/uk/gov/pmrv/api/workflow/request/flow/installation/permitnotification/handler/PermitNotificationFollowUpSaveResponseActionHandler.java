package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveResponseRequestTaskActionPayload;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class PermitNotificationFollowUpSaveResponseActionHandler
    implements RequestTaskActionHandler<PermitNotificationFollowUpSaveResponseRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final PermitNotificationFollowUpSaveResponseRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpRequestTaskPayload taskPayload =
            (PermitNotificationFollowUpRequestTaskPayload) requestTask.getPayload();
        
        final String response = actionPayload.getResponse();
        final Set<UUID> files = actionPayload.getFiles();
        
        taskPayload.setFollowUpResponse(response);
        taskPayload.setFollowUpFiles(files);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE);
    }
}
