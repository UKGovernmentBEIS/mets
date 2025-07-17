package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AviationAccountCompetentAuthorityDTOByRequestResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaOfficialNoticeSendServiceTest {

    @InjectMocks
    private AviationDoECorsiaOfficialNoticeSendService officialNoticeSendService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private FileDocumentService fileDocumentService;

    @Mock
    private AviationAccountCompetentAuthorityDTOByRequestResolver caResolver;

    @Test
    void sendOfficialNotice() {
        String requestId = "REQ-ID";
        String decisionNotificationSignatory = "signatory_user";
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(decisionNotificationSignatory)
                .build();
        FileInfoDTO officialNoticeFileInfoDTO = FileInfoDTO.builder()
                .name("DoE_notice.pdf.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .officialNotice(officialNoticeFileInfoDTO)
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .competentAuthority(CompetentAuthorityEnum.WALES)
                .type(RequestType.AVIATION_DOE_CORSIA)
                .build();
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().firstName("fn").lastName("ln").email("email").build();
        UserInfoDTO accountServiceContact = UserInfoDTO.builder().firstName("fn2").lastName("ln2").email("email2").build();
        List<String> toRecipientEmails = List.of(accountPrimaryContact.getEmail(), accountServiceContact.getEmail());
        List<String> ccRecipientsEmails = List.of("emailRecipient1", "emailRecipient2");
        FileDTO officialNoticeFileDTO = FileDTO.builder().fileContent("content".getBytes()).build();
        CompetentAuthorityDTO competentAuthority = CompetentAuthorityDTO.builder()
                .id(CompetentAuthorityEnum.WALES)
                .name("competentAuthority")
                .email("competent@authority.com")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));
        when(requestAccountContactQueryService.getRequestAccountServiceContact(request)).thenReturn(Optional.of(accountServiceContact));
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(ccRecipientsEmails);
        when(fileDocumentService.getFileDTO(officialNoticeFileInfoDTO.getUuid())).thenReturn(officialNoticeFileDTO);
        when(caResolver.resolveCA(request)).thenReturn(competentAuthority);

        //invoke
        officialNoticeSendService.sendOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountServiceContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(fileDocumentService, times(1)).getFileDTO(officialNoticeFileInfoDTO.getUuid());
        verify(caResolver, times(1)).resolveCA(request);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
                Mockito.eq(toRecipientEmails), Mockito.eq((ccRecipientsEmails)));
        EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
        assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
                        .competentAuthority(CompetentAuthorityEnum.WALES)
                        .accountType(AccountType.AVIATION)
                        .templateParams(Map.of(
                                PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT, accountPrimaryContact.getFullName(),
                                PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail(),
                                PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName()
                        ))
                        .build())
                .attachments(Map.of(officialNoticeFileInfoDTO.getName(), officialNoticeFileDTO.getFileContent())).build());
    }

    @Test
    void sendOfficialNotice_nor_primary_contact_neither_other_users_selected_to_be_notified() {
        String requestId = "REQ-ID";
        String decisionNotificationSignatory = "signatory_user";
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(decisionNotificationSignatory)
                .build();
        FileInfoDTO officialNoticeFileInfoDTO = FileInfoDTO.builder()
                .name("DoE_notice.pdf.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .officialNotice(officialNoticeFileInfoDTO)
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .competentAuthority(CompetentAuthorityEnum.WALES)
                .type(RequestType.AVIATION_DOE_CORSIA)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.empty());
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(Collections.emptyList());

        //invoke
        officialNoticeSendService.sendOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);

        verifyNoMoreInteractions(requestAccountContactQueryService);
        verifyNoInteractions(fileDocumentService);
        verifyNoInteractions(notificationEmailService);
        verifyNoInteractions(caResolver);
    }

    @Test
    void sendOfficialNotice_no_primary_contact_exists_but_other_users_selected_to_be_notified() {
        String requestId = "REQ-ID";
        String decisionNotificationSignatory = "signatory_user";
        Long externalContact = 1L;
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(decisionNotificationSignatory)
                .externalContacts(Set.of(externalContact))
                .build();
        FileInfoDTO officialNoticeFileInfoDTO = FileInfoDTO.builder()
                .name("DoE_notice.pdf.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .officialNotice(officialNoticeFileInfoDTO)
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .competentAuthority(CompetentAuthorityEnum.WALES)
                .type(RequestType.AVIATION_DOE_CORSIA)
                .build();
        List<String> ccRecipientsEmails = List.of("emailRecipient1");
        FileDTO officialNoticeFileDTO = FileDTO.builder().fileContent("content".getBytes()).build();
        CompetentAuthorityDTO competentAuthority = CompetentAuthorityDTO.builder()
                .id(CompetentAuthorityEnum.WALES)
                .name("competentAuthority")
                .email("competent@authority.com")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.empty());
        when(decisionNotificationUsersService.findUserEmails(decisionNotification)).thenReturn(ccRecipientsEmails);
        when(fileDocumentService.getFileDTO(officialNoticeFileInfoDTO.getUuid())).thenReturn(officialNoticeFileDTO);
        when(caResolver.resolveCA(request)).thenReturn(competentAuthority);

        //invoke
        officialNoticeSendService.sendOfficialNotice(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
        verify(fileDocumentService, times(1)).getFileDTO(officialNoticeFileInfoDTO.getUuid());
        verify(caResolver, times(1)).resolveCA(request);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1))
                .notifyRecipients(emailDataCaptor.capture(), Mockito.eq(Collections.emptyList()), Mockito.eq((ccRecipientsEmails)));
        EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
        assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
                        .competentAuthority(CompetentAuthorityEnum.WALES)
                        .accountType(AccountType.AVIATION)
                        .templateParams(Map.of(
                                PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail(),
                                PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME, competentAuthority.getName()
                        ))
                        .build())
                .attachments(Map.of(officialNoticeFileInfoDTO.getName(), officialNoticeFileDTO.getFileContent())).build());

        verifyNoMoreInteractions(requestAccountContactQueryService);
    }
}
