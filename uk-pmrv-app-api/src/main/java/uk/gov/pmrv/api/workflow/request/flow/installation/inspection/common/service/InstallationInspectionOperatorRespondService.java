package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.mapper.InstallationInspectionMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public abstract class InstallationInspectionOperatorRespondService {
    private static final InstallationInspectionMapper INSTALLATION_INSPECTION_MAPPER =
            Mappers.getMapper(InstallationInspectionMapper.class);
    private final RequestService requestService;
    private final AuthorityService authorityService;
    private final UserAuthService userAuthService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
    private final WebAppProperties webAppProperties;

    public void applySaveAction(RequestTask requestTask,
                                InstallationInspectionOperatorRespondSaveRequestTaskActionPayload taskActionPayload) {
        final InstallationInspectionOperatorRespondRequestTaskPayload taskPayload =
                (InstallationInspectionOperatorRespondRequestTaskPayload) requestTask.getPayload();

        taskPayload.setFollowupActionsResponses(taskActionPayload.getFollowupActionsResponses());
        taskPayload.setInstallationInspectionOperatorRespondSectionsCompleted(
                taskActionPayload.getInstallationInspectionOperatorRespondSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(final RequestTask requestTask,
                                    final AppUser appUser) {

        final Request request = requestTask.getRequest();
        final InstallationInspectionOperatorRespondRequestTaskPayload taskPayload =
               (InstallationInspectionOperatorRespondRequestTaskPayload) requestTask.getPayload();


        final InstallationInspectionOperatorRespondedRequestActionPayload actionPayload =
               INSTALLATION_INSPECTION_MAPPER.toInstallationInspectionOperatorRespondedRequestActionPayload(
                   taskPayload);

        requestService.addActionToRequest(
               request,
               actionPayload,
               getOperatorResponedRequestActionType(),
               appUser.getUserId());

        final InstallationInspectionRequestPayload requestPayload =
                (InstallationInspectionRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setInstallationInspectionOperatorRespondSectionsCompleted(
                taskPayload.getInstallationInspectionOperatorRespondSectionsCompleted());
        requestPayload.setFollowupActionsResponses(taskPayload.getFollowupActionsResponses());
        requestPayload.setInspectionAttachments(taskPayload.getInspectionAttachments());

        sendSubmittedOperatorResponseNotificationToRegulator(request);
    }

    private void sendSubmittedOperatorResponseNotificationToRegulator(Request request) {

        final Set<String> recipientsEmails = new HashSet<>();
        final String reviewer = request.getPayload().getRegulatorReviewer();
        final String regulator = request.getPayload().getRegulatorAssignee();
        final Long accountId = request.getAccountId();

        if (!ObjectUtils.isEmpty(reviewer)) {
            Optional.ofNullable(authorityService.findStatusByUsers(List.of(reviewer)).get(reviewer))
                    .ifPresent(reviewerStatus -> {
                        if (AuthorityStatus.ACTIVE.equals(reviewerStatus)) {
                            UserInfoDTO userReviewer = userAuthService.getUserByUserId(reviewer);
                            recipientsEmails.add(userReviewer.getEmail());
                        }
                    });
        }

        if (!ObjectUtils.isEmpty(regulator)) {
            Optional.ofNullable(authorityService.findStatusByUsers(List.of(regulator)).get(regulator))
                    .ifPresent(reviewerStatus -> {
                        if (AuthorityStatus.ACTIVE.equals(reviewerStatus)) {
                            UserInfoDTO regulatorUser = userAuthService.getUserByUserId(regulator);
                            recipientsEmails.add(regulatorUser.getEmail());
                        }
                    });
        }

        if (!recipientsEmails.isEmpty()) {
            InstallationOperatorDetails operatorDetails = installationOperatorDetailsQueryService
                    .getInstallationOperatorDetails(accountId);

            EmailData<EmailNotificationTemplateData> notifyInfo = EmailData.<EmailNotificationTemplateData>builder()
                    .notificationTemplateData(EmailNotificationTemplateData.builder()
                            .templateName(
                                    PmrvNotificationTemplateName.INSTALLATION_INSPECTION_NOTIFICATION_OPERATOR_RESPONSE.getName())
                            .templateParams(Map.of(
                            		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME,
                                    operatorDetails.getInstallationName(),
                                    PmrvEmailNotificationTemplateConstants.EMITTER_ID,
                                    operatorDetails.getEmitterId(),
                                    PmrvEmailNotificationTemplateConstants.HOME_URL,
                                    webAppProperties.getUrl()
                            ))
                            .build())
                    .build();

            notificationEmailService.notifyRecipients(notifyInfo, new ArrayList<>(recipientsEmails));
        }
    }

    protected abstract RequestActionType getOperatorResponedRequestActionType();
}
