package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.mapper.PermitNotificationMapper;

@Service
@RequiredArgsConstructor
public class RequestPermitNotificationService {

    private final RequestService requestService;
    private final PermitNotificationSubmitValidatorService permitNotificationSubmitValidatorService;
    private static final PermitNotificationMapper permitNotificationMapper = Mappers.getMapper(PermitNotificationMapper.class);

    @Transactional
    public void applySavePayload(PermitNotificationSaveApplicationRequestTaskActionPayload actionPayload,
                                 RequestTask requestTask) {

        PermitNotificationApplicationSubmitRequestTaskPayload taskPayload =
                (PermitNotificationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Create new payload if it doesn't exist
        if (taskPayload == null) {
            taskPayload = PermitNotificationApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD)
                    .build();
            requestTask.setPayload(taskPayload);
        }

        // Block new TEMPORARY_SUSPENSION notifications, but allow existing ones
        if (actionPayload.getPermitNotification() != null) {
            boolean isNewTemporarySuspension = PermitNotificationType.TEMPORARY_SUSPENSION.equals(
                    actionPayload.getPermitNotification().getType());

            boolean isExistingTemporarySuspension = taskPayload.getPermitNotification() != null &&
                    PermitNotificationType.TEMPORARY_SUSPENSION.equals(taskPayload.getPermitNotification().getType());

            if (isNewTemporarySuspension && !isExistingTemporarySuspension) {
                throw new BusinessException(MetsErrorCode.INVALID_PERMIT_NOTIFICATION_NOT_SUPPORTED_TEMPORARY_SUSPENSION);
            }
        }

        taskPayload.setPermitNotification(actionPayload.getPermitNotification());
        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
    }

    @Transactional
    public void applySubmitPayload(RequestTask requestTask, AppUser authUser) {
        Request request = requestTask.getRequest();
        PermitNotificationApplicationSubmitRequestTaskPayload
                taskPayload = (PermitNotificationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        PermitNotificationContainer permitNotificationContainer = permitNotificationMapper.toPermitNotificationContainer(taskPayload);

        // Validate permit notification
        permitNotificationSubmitValidatorService.validatePermitNotification(permitNotificationContainer);

        // Update request payload
        PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        requestPayload.setPermitNotification(taskPayload.getPermitNotification());
        requestPayload.setPermitNotificationAttachments(taskPayload.getPermitNotificationAttachments());

        // Add action
        PermitNotificationApplicationSubmittedRequestActionPayload applicationSubmittedActionPayload = permitNotificationMapper.toApplicationSubmittedRequestActionPayload(taskPayload);
        applicationSubmittedActionPayload.setPermitNotificationAttachments(requestPayload.getPermitNotificationAttachments());
        requestService.addActionToRequest(request, applicationSubmittedActionPayload, RequestActionType.PERMIT_NOTIFICATION_APPLICATION_SUBMITTED, authUser.getUserId());
    }
}
