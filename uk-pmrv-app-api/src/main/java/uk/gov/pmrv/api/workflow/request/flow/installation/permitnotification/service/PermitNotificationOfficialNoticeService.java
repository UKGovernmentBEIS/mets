package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermitNotificationOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final NotificationEmailService notificationEmailService;
    private final RequestService requestService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;

    public void sendFollowUpOfficialNotice(final Request request) {

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getFollowUpReviewDecisionNotification())
        	.stream()
            .filter(email -> !email.equals(accountPrimaryContact.getEmail()))
            .collect(Collectors.toList());

        // notify 
        notificationEmailService.notifyRecipients(
            EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(NotificationTemplateName.PERMIT_NOTIFICATION_OPERATOR_RESPONSE)
                    .competentAuthority(request.getCompetentAuthority())
                    .accountType(request.getType().getAccountType())
                    .templateParams(
                        Map.of(
                            EmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT, accountPrimaryContact.getFullName(),
                            EmailNotificationTemplateConstants.WORKFLOW_ID, request.getId()
                        )).build())
                .build(),
            List.of(accountPrimaryContact.getEmail()),
            ccRecipientsEmails);
    }

    public void sendOfficialNotice(final Request request) {
        
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getReviewDecisionNotification());
        officialNoticeSendService.sendOfficialNotice(List.of(requestPayload.getOfficialNotice()), request, ccRecipientsEmails);
    }

    @Transactional
    public void generateAndSaveGrantedOfficialNotice(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getReviewDecisionNotification());

        //generate
        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
            accountPrimaryContact,
            ccRecipientsEmails,
            DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_GRANTED,
            DocumentTemplateType.PERMIT_NOTIFICATION_ACCEPTED,
            "Permit Notification Acknowledgement Letter.pdf");

        //save to payload
        requestPayload.setOfficialNotice(officialNotice);
    }

    @Transactional
    public void generateAndSaveRejectedOfficialNotice(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getReviewDecisionNotification());

        //generate
        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
            accountPrimaryContact,
            ccRecipientsEmails,
            DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_REJECTED,
            DocumentTemplateType.PERMIT_NOTIFICATION_REFUSED,
            "Permit Notification Refusal Letter.pdf");

        //save to payload
        requestPayload.setOfficialNotice(officialNotice);
    }

    private FileInfoDTO generateOfficialNotice(final Request request, 
                                               final UserInfoDTO accountPrimaryContact,
                                               final List<String> ccRecipientsEmails,
                                               final DocumentTemplateGenerationContextActionType type,
                                               final DocumentTemplateType documentTemplateType, 
                                               final String fileNameToGenerate) {
        
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();

        final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(type)
                .request(request)
                .signatory(requestPayload.getReviewDecisionNotification().getSignatory())
                .accountPrimaryContact(accountPrimaryContact)
                .toRecipientEmail(accountPrimaryContact.getEmail())
                .ccRecipientsEmails(ccRecipientsEmails)
                .build()
        );
        return documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }
}
