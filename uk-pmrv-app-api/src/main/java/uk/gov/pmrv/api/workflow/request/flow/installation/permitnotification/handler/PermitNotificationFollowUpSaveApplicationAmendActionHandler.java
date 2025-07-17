package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermitNotificationFollowUpSaveApplicationAmendActionHandler implements
    RequestTaskActionHandler<PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setFollowUpResponse(actionPayload.getResponse());
        taskPayload.setFollowUpFiles(actionPayload.getFiles());
        taskPayload.setReviewSectionsCompleted(actionPayload.getReviewSectionsCompleted());
        taskPayload.setFollowUpSectionsCompleted(actionPayload.getFollowUpSectionsCompleted());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND);
    }
}
