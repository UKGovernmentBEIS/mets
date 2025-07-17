package uk.gov.pmrv.api.workflow.request.flow.aviation.noncompliance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;
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
class AviationNonComplianceSendOfficialNoticeServiceTest {

	@InjectMocks
    private AviationNonComplianceSendOfficialNoticeService officialNoticeSendService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private AviationAccountCompetentAuthorityDTOByRequestResolver caResolver;

    @Test
    void sendOfficialNotice() {
        String requestId = "REQ-ID";
        UUID uuid = UUID.randomUUID();
        FileDTO officialNoticeFileDTO = FileDTO.builder()
        		.fileName("name")
        		.fileContent("content".getBytes())
        		.build();
        NonComplianceDecisionNotification nonComplianceDecisionNotification = 
        		NonComplianceDecisionNotification.builder()
        		.operators(Set.of("operator"))
        		.externalContacts(Set.of(1L))
        		.build();
        DecisionNotification genericDecisionNotification = DecisionNotification.builder()
                .operators(nonComplianceDecisionNotification.getOperators())
                .externalContacts(nonComplianceDecisionNotification.getExternalContacts())
                .build();
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .type(RequestType.AVIATION_NON_COMPLIANCE)
            .build();
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().firstName("fn").lastName("ln").email("email").build();
        UserInfoDTO accountServiceContact = UserInfoDTO.builder().firstName("fn2").lastName("ln2").email("email2").build();
        List<String> toRecipientEmails = List.of(accountPrimaryContact.getEmail(), accountServiceContact.getEmail());
        List<String> ccRecipientsEmails = List.of("emailRecipient1", "emailRecipient2");
        CompetentAuthorityDTO competentAuthority = CompetentAuthorityDTO.builder()
            .id(CompetentAuthorityEnum.WALES)
            .name("competentAuthority")
            .email("competent@authority.com")
            .build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));
        when(requestAccountContactQueryService.getRequestAccountServiceContact(request)).thenReturn(Optional.of(accountServiceContact));
        when(decisionNotificationUsersService.findUserEmails(genericDecisionNotification)).thenReturn(ccRecipientsEmails);
        when(fileAttachmentService.getFileDTO(uuid.toString())).thenReturn(officialNoticeFileDTO);
        when(caResolver.resolveCA(request)).thenReturn(competentAuthority);

        //invoke
        officialNoticeSendService.sendOfficialNotice(uuid, request, nonComplianceDecisionNotification);

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountServiceContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(genericDecisionNotification);
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
            .attachments(Map.of(officialNoticeFileDTO.getFileName(), officialNoticeFileDTO.getFileContent())).build());
    }

    @Test
    void sendOfficialNotice_nor_primary_contact_neither_other_users_selected_to_be_notified() {
    	String requestId = "REQ-ID";
        UUID uuid = UUID.randomUUID();
        NonComplianceDecisionNotification nonComplianceDecisionNotification = 
        		NonComplianceDecisionNotification.builder()
        		.operators(Set.of("operator"))
        		.externalContacts(Set.of(1L))
        		.build();
        DecisionNotification genericDecisionNotification = DecisionNotification.builder()
                .operators(nonComplianceDecisionNotification.getOperators())
                .externalContacts(nonComplianceDecisionNotification.getExternalContacts())
                .build();
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .type(RequestType.AVIATION_NON_COMPLIANCE)
            .build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.empty());
        when(decisionNotificationUsersService.findUserEmails(genericDecisionNotification)).thenReturn(Collections.emptyList());

        //invoke
        officialNoticeSendService.sendOfficialNotice(uuid, request, nonComplianceDecisionNotification);

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(genericDecisionNotification);

        verifyNoMoreInteractions(requestAccountContactQueryService);
        verifyNoInteractions(notificationEmailService);
        verifyNoInteractions(caResolver);
    }

    @Test
    void sendOfficialNotice_no_primary_contact_exists_but_other_users_selected_to_be_notified() {
    	String requestId = "REQ-ID";
        UUID uuid = UUID.randomUUID();
        FileDTO officialNoticeFileDTO = FileDTO.builder()
        		.fileName("name")
        		.fileContent("content".getBytes())
        		.build();
        NonComplianceDecisionNotification nonComplianceDecisionNotification = 
        		NonComplianceDecisionNotification.builder()
        		.operators(Set.of("operator"))
        		.externalContacts(Set.of(1L))
        		.build();
        DecisionNotification genericDecisionNotification = DecisionNotification.builder()
                .operators(nonComplianceDecisionNotification.getOperators())
                .externalContacts(nonComplianceDecisionNotification.getExternalContacts())
                .build();
        Request request = Request.builder()
            .id(requestId)
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .type(RequestType.AVIATION_NON_COMPLIANCE)
            .build();
        List<String> ccRecipientsEmails = List.of("emailRecipient1");
        CompetentAuthorityDTO competentAuthority = CompetentAuthorityDTO.builder()
            .id(CompetentAuthorityEnum.WALES)
            .name("competentAuthority")
            .email("competent@authority.com")
            .build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.empty());
        when(decisionNotificationUsersService.findUserEmails(genericDecisionNotification)).thenReturn(ccRecipientsEmails);
        when(fileAttachmentService.getFileDTO(uuid.toString())).thenReturn(officialNoticeFileDTO);
        when(caResolver.resolveCA(request))
        	.thenReturn(competentAuthority);

        //invoke
        officialNoticeSendService.sendOfficialNotice(uuid, request, nonComplianceDecisionNotification);

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(decisionNotificationUsersService, times(1)).findUserEmails(genericDecisionNotification);
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
            .attachments(Map.of(officialNoticeFileDTO.getFileName(), officialNoticeFileDTO.getFileContent())).build());

        verifyNoMoreInteractions(requestAccountContactQueryService);
    }
    
    @Test
    void getAccountType() {
    	AccountType type = officialNoticeSendService.getAccountType();
    	
    	assertThat(type).isEqualTo(AccountType.AVIATION);
    }
}
