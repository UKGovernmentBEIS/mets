package uk.gov.pmrv.api.workflow.request.flow.aviation.noncompliance.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceSendOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AviationAccountCompetentAuthorityDTOByRequestResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AviationNonComplianceSendOfficialNoticeService implements NonComplianceSendOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final AviationAccountCompetentAuthorityDTOByRequestResolver caResolver;
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

        EmailData<PmrvEmailNotificationTemplateData> notificationEmailData = buildEmailData(request, file, Optional.of(accountPrimaryContact));

        notificationEmailService.notifyRecipients(notificationEmailData, toRecipientEmails, ccRecipientsEmails);
    }

    private void sendOfficialNoticeCcRecipientsOnly(Request request, List<String> ccRecipientsEmails, FileDTO file) {
        EmailData<PmrvEmailNotificationTemplateData> notificationEmailData = buildEmailData(request, file, Optional.empty());

        notificationEmailService.notifyRecipients(notificationEmailData, Collections.emptyList(), ccRecipientsEmails);
    }

    private EmailData<PmrvEmailNotificationTemplateData> buildEmailData(Request request, FileDTO file, Optional<UserInfoDTO> accountPrimaryContactUserInfo) {
        Map<String, Object> templateParams = new HashMap<>();
        accountPrimaryContactUserInfo
            .ifPresent(userInfo -> templateParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT, userInfo.getFullName()));

        CompetentAuthorityDTO competentAuthority = caResolver.resolveCA(request);

        templateParams.put(PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail());
        templateParams.put(PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName());

        return EmailData.<PmrvEmailNotificationTemplateData>builder()
            .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                .templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
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
