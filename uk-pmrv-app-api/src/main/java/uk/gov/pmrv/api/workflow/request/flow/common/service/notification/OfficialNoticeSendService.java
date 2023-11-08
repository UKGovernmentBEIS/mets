package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityService;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OfficialNoticeSendService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final NotificationEmailService notificationEmailService;
    private final FileDocumentService fileDocumentService;
    private final CompetentAuthorityService competentAuthorityService;

    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request) {
        this.sendOfficialNotice(attachments, request, List.of());
    }

    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request, List<String> ccRecipientsEmails) {
        this.sendOfficialNotice(attachments, request, List.of(), ccRecipientsEmails);
    }

    public void sendOfficialNotice(List<FileInfoDTO> attachments, Request request, List<String> toRecipientsEmails, List<String> ccRecipientsEmails) {
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        Set<UserInfoDTO> defaultOfficialNoticeRecipients = getDefaultOfficialNoticeRecipients(request);

        Stream<String> defaultRecipientEmails = defaultOfficialNoticeRecipients.stream().map(UserInfoDTO::getEmail);
        final List<String> toRecipientEmailsFinal = Stream.concat(defaultRecipientEmails, toRecipientsEmails.stream())
                .distinct()
                .collect(Collectors.toList());

        List<String> ccRecipientsEmailsFinal = new ArrayList<>(ccRecipientsEmails);
        ccRecipientsEmailsFinal.removeIf(toRecipientEmailsFinal::contains);

        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(EmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT,
            accountPrimaryContact.getFullName());

        CompetentAuthorityDTO competentAuthority = competentAuthorityService
            .getCompetentAuthority(request.getCompetentAuthority(), request.getType().getAccountType());
        templateParams.put(EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail());
        templateParams.put(EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName());

        //notify
        notificationEmailService.notifyRecipients(
                EmailData.builder()
                        .notificationTemplateData(EmailNotificationTemplateData.builder()
                                .templateName(NotificationTemplateName.GENERIC_EMAIL)
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
                toRecipientEmailsFinal,
                ccRecipientsEmailsFinal);
    }

    public Set<UserInfoDTO> getDefaultOfficialNoticeRecipients(Request request) {
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final UserInfoDTO accountServiceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        return Stream.of(accountPrimaryContact, accountServiceContact).collect(Collectors.toSet());
    }
}
