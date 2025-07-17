package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.util.Date;



@RequiredArgsConstructor
public abstract class InstallationInspectionExpirationDateService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RequestExpirationReminderService requestExpirationReminderService;

    public Date calculateExpirationDate(final String requestId) {
        final Request request = requestService.findRequestById(requestId);

        final InstallationInspectionRequestPayload payload =
            (InstallationInspectionRequestPayload) request.getPayload();

        return DateUtils.atEndOfDay(payload.getInstallationInspection().getResponseDeadline());
    }

    public void sendRespondFirstReminderNotification(final String requestId, final Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.FIRST_REMINDER,
                NotificationTemplateWorkflowTaskType.INSTALLATION_INSPECTION_OPERATOR_RESPOND);
    }

    public void sendRespondSecondReminderNotification(final String requestId, final Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.SECOND_REMINDER,
                NotificationTemplateWorkflowTaskType.INSTALLATION_INSPECTION_OPERATOR_RESPOND);
    }

    private void sendReminderNotification(final String requestId,
                                          final Date deadline,
                                          final ExpirationReminderType expirationType,
                                          final NotificationTemplateWorkflowTaskType workflowTaskType) {

        final Request request = requestService.findRequestById(requestId);
        final UserInfoDTO accountPrimaryContact =
                requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        requestExpirationReminderService.sendExpirationReminderNotification(requestId,
                NotificationTemplateExpirationReminderParams.builder()
                        .workflowTask(workflowTaskType.getDescription())
                        .recipient(accountPrimaryContact)
                        .expirationTime(expirationType.getDescription())
                        .expirationTimeLong(expirationType.getDescriptionLong())
                        .deadline(deadline)
                        .build());
    }

    protected abstract RequestTaskType getRequestTaskType();
}
