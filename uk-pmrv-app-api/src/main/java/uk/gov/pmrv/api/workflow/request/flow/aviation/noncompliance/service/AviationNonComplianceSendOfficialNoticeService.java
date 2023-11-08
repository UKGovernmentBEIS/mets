package uk.gov.pmrv.api.workflow.request.flow.aviation.noncompliance.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AviationNonComplianceSendOfficialNoticeService implements NonComplianceSendOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final NotificationEmailService notificationEmailService;
    private final CompetentAuthorityService competentAuthorityService;
    private final FileAttachmentService fileAttachmentService;

    public void sendOfficialNotice(UUID officialNotice, Request request, NonComplianceDecisionNotification decisionNotification) {
        Optional<UserInfoDTO> requestAccountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);
        FileDTO fileDTO = fileAttachmentService.getFileDTO(officialNotice.toString());
        DecisionNotification genericDecisionNotification = DecisionNotification.builder()
                .operators(decisionNotification.getOperators())
                .externalContacts(decisionNotification.getExternalContacts())
                .build();

        requestAccountPrimaryContact.ifPresentOrElse(
            accountPrimaryContact -> sendOfficialNotice(request, accountPrimaryContact, genericDecisionNotification, fileDTO),
            () -> {
                List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(genericDecisionNotification);
                if(ObjectUtils.isNotEmpty(ccRecipientsEmails)) {
                    sendOfficialNoticeCcRecipientsOnly(request, ccRecipientsEmails, fileDTO);
                }
            });
    }

    private void sendOfficialNotice(Request request, UserInfoDTO accountPrimaryContact, DecisionNotification decisionNotification, FileDTO file) {
        UserInfoDTO accountServiceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));

        List<String> toRecipientEmails = Stream.of(accountPrimaryContact.getEmail(), accountServiceContact.getEmail()).distinct().toList();
        List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(decisionNotification);

        EmailData notificationEmailData = buildEmailData(request, file, Optional.of(accountPrimaryContact));

        notificationEmailService.notifyRecipients(notificationEmailData, toRecipientEmails, ccRecipientsEmails);
    }

    private void sendOfficialNoticeCcRecipientsOnly(Request request, List<String> ccRecipientsEmails, FileDTO file) {
        EmailData notificationEmailData = buildEmailData(request, file, Optional.empty());

        notificationEmailService.notifyRecipients(notificationEmailData, Collections.emptyList(), ccRecipientsEmails);
    }

    private EmailData buildEmailData(Request request, FileDTO file, Optional<UserInfoDTO> accountPrimaryContactUserInfo) {
        Map<String, Object> templateParams = new HashMap<>();
        accountPrimaryContactUserInfo
            .ifPresent(userInfo -> templateParams.put(EmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT, userInfo.getFullName()));

        CompetentAuthorityDTO competentAuthority = competentAuthorityService
            .getCompetentAuthority(request.getCompetentAuthority(), request.getType().getAccountType());

        templateParams.put(EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail());
        templateParams.put(EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName());

        return EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                .templateName(NotificationTemplateName.GENERIC_EMAIL)
                .competentAuthority(request.getCompetentAuthority())
                .accountType(request.getType().getAccountType())
                .templateParams(templateParams)
                .build())
            .attachments(Map.of(
                    file.getFileName(),
                    file.getFileContent())
            )
            .build();
    }

	@Override
	public AccountType getAccountType() {
		return AccountType.AVIATION;
	}
}
