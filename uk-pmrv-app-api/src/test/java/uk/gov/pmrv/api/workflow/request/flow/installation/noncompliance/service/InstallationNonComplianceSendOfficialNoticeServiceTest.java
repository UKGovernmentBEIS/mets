package uk.gov.pmrv.api.workflow.request.flow.installation.noncompliance.service;

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
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.InstallationAccountCompetentAuthorityDTOByRequestResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;

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
class InstallationNonComplianceSendOfficialNoticeServiceTest {

    @InjectMocks
    private InstallationNonComplianceSendOfficialNoticeService service;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private DecisionNotificationUsersService decisionNotificationUsersService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private InstallationAccountCompetentAuthorityDTOByRequestResolver caResolver;

    @Test
    void sendOfficialNotice() {

        final UUID uuid = UUID.randomUUID();

        final FileDTO fileDTO = FileDTO.builder()
            .fileName("offDoc.pdf")
            .fileContent("content" .getBytes())
            .build();

        final Request request = Request.builder()
            .id("1")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .type(RequestType.PERMIT_ISSUANCE)
            .build();

        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("primary@email").userId("primaryUserId")
            .build();

        final NonComplianceDecisionNotification decisionNotification =
            NonComplianceDecisionNotification.builder()
                .operators(Set.of("operator"))
                .externalContacts(Set.of(1L))
                .build();

        final DecisionNotification genericDecisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .externalContacts(Set.of(1L))
            .build();

        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));
        when(requestAccountContactQueryService.getRequestAccountContact(request, AccountContactType.SERVICE)).thenReturn(
            Optional.of(UserInfoDTO.builder().email("service@email.com").build()));
        when(decisionNotificationUsersService.findUserEmails(genericDecisionNotification)).thenReturn(List.of("operator@mail.com", "externalContact@mail.com"));
        when(fileAttachmentService.getFileDTO(uuid.toString())).thenReturn(fileDTO);
        when(caResolver.resolveCA(request))
            .thenReturn(CompetentAuthorityDTO.builder().name("ca name").email("ca@mail.com").build());

        service.sendOfficialNotice(uuid, request, decisionNotification);

        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);
        verify(fileAttachmentService, times(1)).getFileDTO(uuid.toString());
        verify(caResolver, times(1)).resolveCA(request);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipients(emailDataCaptor.capture(),
            Mockito.eq(List.of(accountPrimaryContact.getEmail(), "service@email.com")), Mockito.eq(List.of("operator@mail.com", "externalContact@mail.com")));
        
        EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
        assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
            .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                .templateName(PmrvNotificationTemplateName.GENERIC_EMAIL.getName())
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .accountType(AccountType.INSTALLATION)
                .templateParams(Map.of(
                		PmrvEmailNotificationTemplateConstants.ACCOUNT_PRIMARY_CONTACT,
                    accountPrimaryContact.getFullName(),
                    PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME,
                    "ca name",
                    PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL,
                    "ca@mail.com"))
                .build())
            .attachments(Map.of(fileDTO.getFileName(), fileDTO.getFileContent())).build());


    }
}
