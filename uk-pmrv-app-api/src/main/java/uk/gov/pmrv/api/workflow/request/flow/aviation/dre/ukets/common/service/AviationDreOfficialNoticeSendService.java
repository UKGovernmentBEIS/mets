package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AviationDreOfficialNoticeSendService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final NotificationEmailService notificationEmailService;
    private final FileDocumentService fileDocumentService;
    private final CompetentAuthorityService competentAuthorityService;

    public void sendOfficialNotice(String requestId) {
        Request request = requestService.findRequestById(requestId);
        Optional<UserInfoDTO> requestAccountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);

        requestAccountPrimaryContact.ifPresentOrElse(
            accountPrimaryContact -> sendOfficialNotice(request, accountPrimaryContact),
            () -> {
                AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();
                List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
                if(ObjectUtils.isNotEmpty(ccRecipientsEmails)) {
                    sendOfficialNoticeCcRecipientsOnly(request, ccRecipientsEmails, List.of(requestPayload.getOfficialNotice()));
                }
            });
    }

    private void sendOfficialNotice(Request request, UserInfoDTO accountPrimaryContact) {
        AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();
        UserInfoDTO accountServiceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));

        List<String> toRecipientEmails = Stream.of(accountPrimaryContact.getEmail(), accountServiceContact.getEmail()).distinct().toList();
        List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        EmailData notificationEmailData = buildEmailData(request, List.of(requestPayload.getOfficialNotice()), Optional.of(accountPrimaryContact));

        notificationEmailService.notifyRecipients(notificationEmailData, toRecipientEmails, ccRecipientsEmails);
    }

    private void sendOfficialNoticeCcRecipientsOnly(Request request, List<String> ccRecipientsEmails, List<FileInfoDTO> attachments) {
        EmailData notificationEmailData = buildEmailData(request, attachments, Optional.empty());

        notificationEmailService.notifyRecipients(notificationEmailData, Collections.emptyList(), ccRecipientsEmails);
    }

    private EmailData buildEmailData(Request request, List<FileInfoDTO> attachments, Optional<UserInfoDTO> accountPrimaryContactUserInfo) {
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
            .attachments(attachments.stream().collect(
                Collectors.toMap(FileInfoDTO::getName, att -> fileDocumentService.getFileDTO(att.getUuid()).getFileContent()))
            )
            .build();
    }
}
