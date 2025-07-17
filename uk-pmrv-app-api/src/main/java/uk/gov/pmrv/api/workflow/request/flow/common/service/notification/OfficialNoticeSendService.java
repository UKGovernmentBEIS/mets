package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CompetentAuthorityDTOByRequestResolverDelegator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OfficialNoticeSendService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final FileDocumentService fileDocumentService;
    private final CompetentAuthorityDTOByRequestResolverDelegator competentAuthorityDTOByRequestResolverDelegator;
    private final AccountQueryService accountQueryService;

    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request) {
        this.sendOfficialNotice(attachments, request, List.of(), List.of());
    }
    
    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request, 
			List<String> ccRecipientsEmails) {
    	this.sendOfficialNotice(attachments, request, ccRecipientsEmails, Collections.emptyList());
    }

	public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request, 
			List<String> ccRecipientsEmails, List<String> bccRecipientsEmails) {
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

		final List<String> toRecipientsEmails = getOfficialNoticeToRecipients(request).stream()
				.map(UserInfoDTO::getEmail)
				.collect(Collectors.toList());

        final List<String> ccRecipientsEmailsFinal = new ArrayList<>(ccRecipientsEmails);
        ccRecipientsEmailsFinal.removeIf(toRecipientsEmails::contains);

        final Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT,
            accountPrimaryContact.getFullName());

        final AccountType accountType = accountQueryService.getAccountType(request.getAccountId());

		final CompetentAuthorityDTO competentAuthority =
                competentAuthorityDTOByRequestResolverDelegator.resolveCA(request, accountType);
		
        templateParams.put(PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail());
        templateParams.put(PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName());

        //notify
        notificationEmailService.notifyRecipients(
                EmailData.<PmrvEmailNotificationTemplateData>builder()
                        .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                                .templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
                                .competentAuthority(request.getCompetentAuthority())
                                .accountType(request.getType().getAccountType())
                                .templateParams(templateParams)
                                .build())
                        .attachments(attachments.stream().collect(
                                        Collectors.toMap(
                                                FileInfoDTO::getName,
                                                att -> fileDocumentService.getFileDTO(att.getUuid()).getFileContent())
                                )
                        )
                        .build(),
                toRecipientsEmails,
                ccRecipientsEmailsFinal,
                bccRecipientsEmails);
    }

    public Set<UserInfoDTO> getOfficialNoticeToRecipients(Request request) {
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final UserInfoDTO accountServiceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        return Stream.of(accountPrimaryContact, accountServiceContact).collect(Collectors.toSet());
    }
}
