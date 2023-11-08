package uk.gov.pmrv.api.workflow.request.flow.installation.noncompliance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityService;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceSendOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstallationNonComplianceSendOfficialNoticeService implements NonComplianceSendOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final NotificationEmailService notificationEmailService;
    private final FileAttachmentService fileAttachmentService;
    private final CompetentAuthorityService competentAuthorityService;

    public void sendOfficialNotice(final UUID officialNotice,
                                   final Request request,
                                   final NonComplianceDecisionNotification decisionNotification) {

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final String primaryContactEmail = accountPrimaryContact.getEmail();
        final String serviceContactEmail =
            requestAccountContactQueryService.getRequestAccountContact(request, AccountContactType.SERVICE)
                .map(UserInfoDTO::getEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        final List<String> toRecipient = serviceContactEmail != null && !serviceContactEmail.equals(primaryContactEmail) ? 
            List.of(primaryContactEmail, serviceContactEmail) :
            List.of(primaryContactEmail);

        final DecisionNotification genericDecisionNotification = DecisionNotification.builder()
            .operators(decisionNotification.getOperators())
            .externalContacts(decisionNotification.getExternalContacts())
            .build();
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(genericDecisionNotification);

        final CompetentAuthorityDTO competentAuthority =
            competentAuthorityService.getCompetentAuthority(request.getCompetentAuthority(), request.getType().getAccountType());

        //remove from 'cc' if is included in the 'to' recipient
        List<String> ccRecipientsEmailsFinal = new ArrayList<>(ccRecipientsEmails);
        ccRecipientsEmailsFinal.removeIf(toRecipient::contains);

        final FileDTO fileDTO = fileAttachmentService.getFileDTO(officialNotice.toString());

        //notify 
        notificationEmailService.notifyRecipients(
            EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(NotificationTemplateName.GENERIC_EMAIL)
                    .competentAuthority(request.getCompetentAuthority())
                    .accountType(request.getType().getAccountType())
                    .templateParams(Map.of(
                        EmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT,
                        accountPrimaryContact.getFullName(),
                        EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME,
                        competentAuthority.getName(),
                        EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL,
                        competentAuthority.getEmail()))
                    .build())
                .attachments(Map.of(
                        fileDTO.getFileName(),
                        fileDTO.getFileContent()
                    )
                ).build(),
            toRecipient,
            ccRecipientsEmailsFinal);
    }
    
    @Override
	public AccountType getAccountType() {
		return AccountType.INSTALLATION;
	}
}
