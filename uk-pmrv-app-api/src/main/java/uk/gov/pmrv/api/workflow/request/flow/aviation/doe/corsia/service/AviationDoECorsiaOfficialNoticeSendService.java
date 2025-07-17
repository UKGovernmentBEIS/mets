package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AviationAccountCompetentAuthorityDTOByRequestResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class AviationDoECorsiaOfficialNoticeSendService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final FileDocumentService fileDocumentService;
    private final AviationAccountCompetentAuthorityDTOByRequestResolver caResolver;

    public void sendOfficialNotice(String requestId) {
        Request request = requestService.findRequestById(requestId);
        Optional<UserInfoDTO> requestAccountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);

        requestAccountPrimaryContact.ifPresentOrElse(
                accountPrimaryContact -> sendOfficialNotice(request, accountPrimaryContact),
                () -> {
                    AviationDoECorsiaRequestPayload requestPayload = (AviationDoECorsiaRequestPayload) request.getPayload();
                    List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
                    if(ObjectUtils.isNotEmpty(ccRecipientsEmails)) {
                        sendOfficialNoticeCcRecipientsOnly(request, ccRecipientsEmails, List.of(requestPayload.getOfficialNotice()));
                    }
                });
    }

    private void sendOfficialNotice(Request request, UserInfoDTO accountPrimaryContact) {
        AviationDoECorsiaRequestPayload requestPayload = (AviationDoECorsiaRequestPayload) request.getPayload();
        UserInfoDTO accountServiceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));

        List<String> toRecipientEmails = Stream.of(accountPrimaryContact.getEmail(), accountServiceContact.getEmail()).distinct().toList();
        List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        EmailData<PmrvEmailNotificationTemplateData> notificationEmailData = buildEmailData(request, List.of(requestPayload.getOfficialNotice()), Optional.of(accountPrimaryContact));

        notificationEmailService.notifyRecipients(notificationEmailData, toRecipientEmails, ccRecipientsEmails);
    }

    private void sendOfficialNoticeCcRecipientsOnly(Request request, List<String> ccRecipientsEmails, List<FileInfoDTO> attachments) {
        EmailData<PmrvEmailNotificationTemplateData> notificationEmailData = buildEmailData(request, attachments, Optional.empty());

        notificationEmailService.notifyRecipients(notificationEmailData, Collections.emptyList(), ccRecipientsEmails);
    }

    private EmailData<PmrvEmailNotificationTemplateData> buildEmailData(Request request, List<FileInfoDTO> attachments, Optional<UserInfoDTO> accountPrimaryContactUserInfo) {
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
                .attachments(attachments.stream().collect(
                        Collectors.toMap(FileInfoDTO::getName, att -> fileDocumentService.getFileDTO(att.getUuid()).getFileContent()))
                )
                .build();
    }
}
