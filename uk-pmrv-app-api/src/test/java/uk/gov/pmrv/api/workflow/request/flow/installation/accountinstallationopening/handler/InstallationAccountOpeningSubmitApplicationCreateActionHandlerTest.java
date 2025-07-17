package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountCreationService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.*;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningSubmitApplicationCreateActionHandlerTest {

    @InjectMocks
    private InstallationAccountOpeningSubmitApplicationCreateActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationAccountCreationService installationAccountCreationService;

    @Mock
    private NotificationProperties notificationProperties;

    @Test
    void process_new_permit() {
        String contactUsLink = "/contact-us";
        AppUser appUser = AppUser.builder().userId("user").firstName("fn").lastName("ln").build();
        Long accountId = 1L;
        InstallationAccountPayload accountPayload = InstallationAccountPayload.builder()
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.NEW_PERMIT)
            .name("account")
            .submitter(InstallationAccountSubmitter.builder().name("fn ln").build())
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .location(LocationOnShoreDTO.builder().build())
            .legalEntity(LegalEntityDTO.builder().id(1L).build()).build();

        InstallationAccountOpeningSubmitApplicationCreateActionPayload createActionPayload = InstallationAccountOpeningSubmitApplicationCreateActionPayload
            .builder()
            .payloadType(RequestCreateActionPayloadType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD)
            .accountPayload(accountPayload).build();

        InstallationAccountOpeningRequestPayload requestPayload = InstallationAccountOpeningRequestPayload.builder()
            .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
            .accountPayload(accountPayload)
            .operatorAssignee(appUser.getUserId())
            .build();

        InstallationAccountOpeningApplicationSubmittedRequestActionPayload accountSubmittedPayload = InstallationAccountOpeningApplicationSubmittedRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD)
            .accountPayload(accountPayload)
            .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.NEW_PERMIT)
            .name("account")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .location(LocationOnShoreDTO.builder().build())
            .legalEntity(LegalEntityDTO.builder().id(1L).build()).build();

        InstallationAccountDTO persistedAccountDTO = InstallationAccountDTO.builder()
            .id(accountId)
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.NEW_PERMIT)
            .name("account")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .location(LocationOnShoreDTO.builder().build())
            .legalEntity(LegalEntityDTO.builder().id(1L).name("le").build()).build();

        RequestParams requestParams = RequestParams.builder()
            .type(INSTALLATION_ACCOUNT_OPENING)
            .accountId(accountId)
            .requestPayload(requestPayload)
            .processVars(Map.of(BpmnProcessConstants.APPLICATION_TYPE_IS_TRANSFER, false))
            .build();

        Request request = Request.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).creationDate(LocalDateTime.now()).build();

        when(installationAccountCreationService.createAccount(accountDTO, appUser)).thenReturn(persistedAccountDTO);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);

        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);

        //invoke
        handler.process(null, createActionPayload, appUser);

        assertThat(request.getSubmissionDate()).isEqualTo(request.getCreationDate());
        verify(installationAccountCreationService, times(1)).createAccount(accountDTO, appUser);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(requestService, times(1))
            .addActionToRequest(request,
                accountSubmittedPayload,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
                appUser.getUserId());


        ArgumentCaptor<EmailData<EmailNotificationTemplateData>> recipientEmailInformationCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailInformationCaptor.capture(), Mockito.eq(appUser.getEmail()));
        EmailData<EmailNotificationTemplateData> emailData = recipientEmailInformationCaptor.getValue();
        assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.ACCOUNT_APPLICATION_RECEIVED.getName());
        assertThat(emailData.getNotificationTemplateData().getTemplateParams())
                .isEqualTo(Map.of(PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink));

    }

    @Test
    void process_transfer() {
        String contactUsLink = "/contact-us";
        AppUser appUser = AppUser.builder().userId("user").firstName("fn").lastName("ln").build();
        Long accountId = 1L;
        InstallationAccountPayload accountPayload = InstallationAccountPayload.builder()
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.TRANSFER)
            .name("account")
            .submitter(InstallationAccountSubmitter.builder().name("fn ln").build())
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .location(LocationOnShoreDTO.builder().build())
            .legalEntity(LegalEntityDTO.builder().id(1L).build()).build();

        InstallationAccountOpeningSubmitApplicationCreateActionPayload createActionPayload = InstallationAccountOpeningSubmitApplicationCreateActionPayload
            .builder()
            .payloadType(RequestCreateActionPayloadType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD)
            .accountPayload(accountPayload).build();

        InstallationAccountOpeningRequestPayload requestPayload = InstallationAccountOpeningRequestPayload.builder()
            .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
            .accountPayload(accountPayload)
            .operatorAssignee(appUser.getUserId())
            .build();

        InstallationAccountOpeningApplicationSubmittedRequestActionPayload accountSubmittedPayload = InstallationAccountOpeningApplicationSubmittedRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD)
            .accountPayload(accountPayload)
            .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.TRANSFER)
            .name("account")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .location(LocationOnShoreDTO.builder().build())
            .legalEntity(LegalEntityDTO.builder().id(1L).build()).build();

        InstallationAccountDTO persistedAccountDTO = InstallationAccountDTO.builder()
            .id(accountId)
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.TRANSFER)
            .name("account")
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .location(LocationOnShoreDTO.builder().build())
            .legalEntity(LegalEntityDTO.builder().id(1L).name("le").build()).build();

        RequestParams requestParams = RequestParams.builder()
            .type(INSTALLATION_ACCOUNT_OPENING)
            .accountId(accountId)
            .requestPayload(requestPayload)
            .processVars(Map.of(BpmnProcessConstants.APPLICATION_TYPE_IS_TRANSFER, true))
            .build();

        Request request = Request.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).creationDate(LocalDateTime.now()).build();

        when(installationAccountCreationService.createAccount(accountDTO, appUser)).thenReturn(persistedAccountDTO);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);

        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);

        //invoke
        handler.process(null, createActionPayload, appUser);

        assertThat(request.getSubmissionDate()).isEqualTo(request.getCreationDate());
        verify(installationAccountCreationService, times(1)).createAccount(accountDTO, appUser);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(requestService, times(1))
            .addActionToRequest(request,
                accountSubmittedPayload,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
                appUser.getUserId());


        ArgumentCaptor<EmailData<EmailNotificationTemplateData>> recipientEmailInformationCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailInformationCaptor.capture(), Mockito.eq(appUser.getEmail()));
        EmailData<EmailNotificationTemplateData> emailData = recipientEmailInformationCaptor.getValue();
        assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.ACCOUNT_APPLICATION_RECEIVED.getName());
        assertThat(emailData.getNotificationTemplateData().getTemplateParams())
                .isEqualTo(Map.of(PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink));

    }

}
