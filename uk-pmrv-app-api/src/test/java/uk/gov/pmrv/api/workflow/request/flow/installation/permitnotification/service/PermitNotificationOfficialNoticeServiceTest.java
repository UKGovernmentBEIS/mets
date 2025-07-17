package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationOfficialNoticeServiceTest {

    @InjectMocks
    private PermitNotificationOfficialNoticeService service;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    
    @Mock
    private RequestService requestService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void sendFollowUpOfficialNotice() {

        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
            .followUpReviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        final Request request = Request.builder()
            .id("requestId")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(requestPayload)
            .build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId").build();
        final List<String> ccRecipientsEmails = List.of("operator1@email");

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContact));
        when(decisionNotificationUsersService.findUserEmails(requestPayload.getFollowUpReviewDecisionNotification()))
            .thenReturn(ccRecipientsEmails);

        service.sendFollowUpOfficialNotice(request);

        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1))
            .findUserEmails(requestPayload.getFollowUpReviewDecisionNotification());

        final ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
            Mockito.eq(List.of(accountPrimaryContact.getEmail())), Mockito.eq(List.of("operator1@email")));
        final EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
        assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
            .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                .templateName(PmrvNotificationTemplateName.PERMIT_NOTIFICATION_OPERATOR_RESPONSE.getName())
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .accountType(AccountType.INSTALLATION)
                .templateParams(Map.of(
                		PmrvEmailNotificationTemplateConstants.WORKFLOW_ID, "requestId",
                		PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT,accountPrimaryContact.getFullName()))
                .build()).build());
    }

    @Test
    void generateAndSaveGrantedOfficialNotice() {

        final String requestId = "1";
        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
            .reviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        final Request request = Request.builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .payload(requestPayload)
            .build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
            .build();
        final List<String> ccRecipientsEmails = List.of("operator1@email");
        final DocumentTemplateParamsSourceData
            templateSourceParams = DocumentTemplateParamsSourceData.builder()
            .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_GRANTED)
            .request(request)
            .signatory("signatory")
            .accountPrimaryContact(accountPrimaryContact)
            .toRecipientEmail(accountPrimaryContact.getEmail())
            .ccRecipientsEmails(List.of("operator1@email"))
            .build();
        final FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
            .name("offDoc.pdf")
            .uuid(UUID.randomUUID().toString())
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));
        when(decisionNotificationUsersService.findUserEmails(requestPayload.getReviewDecisionNotification()))
        	.thenReturn(ccRecipientsEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(templateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(DocumentTemplateType.PERMIT_NOTIFICATION_ACCEPTED, templateParams, "Permit Notification Acknowledgement Letter.pdf"))
            .thenReturn(officialDocFileInfoDTO);

        service.generateAndSaveGrantedOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1))
        .findUserEmails(requestPayload.getReviewDecisionNotification());
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(templateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(DocumentTemplateType.PERMIT_NOTIFICATION_ACCEPTED, templateParams, "Permit Notification Acknowledgement Letter.pdf");

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateAndSaveRejectedOfficialNotice() {

        final String requestId = "1";
        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
            .reviewDecisionNotification(DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .signatory("signatory")
                .build())
            .build();
        final Request request = Request.builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .payload(requestPayload)
            .build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
            .build();
        final List<String> ccRecipientsEmails = List.of("operator1@email");
        final DocumentTemplateParamsSourceData
            templateSourceParams = DocumentTemplateParamsSourceData.builder()
            .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_REJECTED)
            .request(request)
            .signatory("signatory")
            .accountPrimaryContact(accountPrimaryContact)
            .toRecipientEmail(accountPrimaryContact.getEmail())
            .ccRecipientsEmails(List.of("operator1@email"))
            .build();
        final FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
            .name("offDoc.pdf")
            .uuid(UUID.randomUUID().toString())
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));
        when(decisionNotificationUsersService.findUserEmails(requestPayload.getReviewDecisionNotification()))
        	.thenReturn(ccRecipientsEmails);
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(templateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(DocumentTemplateType.PERMIT_NOTIFICATION_REFUSED, templateParams, "Permit Notification Refusal Letter.pdf"))
            .thenReturn(officialDocFileInfoDTO);

        service.generateAndSaveRejectedOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1))
        .findUserEmails(requestPayload.getReviewDecisionNotification());
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(templateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(DocumentTemplateType.PERMIT_NOTIFICATION_REFUSED, templateParams, "Permit Notification Refusal Letter.pdf");

        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
}
