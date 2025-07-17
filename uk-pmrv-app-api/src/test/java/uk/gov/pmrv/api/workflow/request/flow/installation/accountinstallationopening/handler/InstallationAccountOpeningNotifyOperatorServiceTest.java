package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.application.UserServiceDelegator;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.AccountOpeningDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningNotifyOperatorServiceTest {

	@InjectMocks
	private InstallationAccountOpeningNotifyOperatorService installationAccountOpeningNotifyOperatorService;
	
	@Mock
	private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
	
	@Mock
	private UserServiceDelegator userServiceDelegator;
	
	@Mock
	private RequestService requestService;

	@Mock
	private WebAppProperties webAppProperties;

	@Mock
	private NotificationProperties notificationProperties;
	
	@Test
	void execute_application_accepted() {
		//prepare data
		final String requestId = "1";
		final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String user = "user";
        final String email = "email@email.gr";
		final String contactUsLink = "/contact-us";
        final String firstName = "fn";
        final String lastName = "ln";
		final Request request = Request.builder()
            .id(requestId)
            .competentAuthority(ca)
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountOpeningDecisionPayload(AccountOpeningDecisionPayload.builder()
                    .decision(Decision.ACCEPTED)
                    .reason("reason")
                    .build())
                .operatorAssignee(user)
                .build())
            .build();
		final UserDTO userDTO = OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();
		String url = "some-url";
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(userServiceDelegator.getUserById(user)).thenReturn(userDTO);
		when(webAppProperties.getUrl()).thenReturn(url);

		NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
		when(notificationProperties.getEmail()).thenReturn(notificationEmail);
		when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
		
		//invoke
		installationAccountOpeningNotifyOperatorService.execute(requestId);
		
		//verify
		verify(requestService, times(1)).findRequestById(requestId);
		verify(userServiceDelegator, times(1)).getUserById(user);
		verify(webAppProperties, times(1)).getUrl();
		
		final ArgumentCaptor<EmailData<EmailNotificationTemplateData>> recipientEmailCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailCaptor.capture(), Mockito.eq(email));
		//assert email argument
		EmailData<EmailNotificationTemplateData> emailData = recipientEmailCaptor.getValue();
		assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.ACCOUNT_APPLICATION_ACCEPTED.getName());
		assertThat(emailData.getNotificationTemplateData().getTemplateParams())
					.containsExactlyInAnyOrderEntriesOf(
							Map.of(
									PmrvEmailNotificationTemplateConstants.HOME_URL, url,
									PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink
									));
	}
	
	@Test
	void execute_application_rejected() {
		//prepare data
		final String requestId = "1";
		final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final String user = "user";
        final String email = "email@email.gr";
		final String contactUsLink = "/contact-us";
        final String firstName = "fn";
        final String lastName = "ln";
        final String rejectionReason = "reason";
		final Request request = Request.builder()
            .id(requestId)
            .competentAuthority(ca)
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountOpeningDecisionPayload(AccountOpeningDecisionPayload.builder()
                    .decision(Decision.REJECTED)
                    .reason(rejectionReason)
                    .build())
                .operatorAssignee(user)
                .build())
            .build();
		final UserDTO userDTO = OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();
		String url = "some-url";
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(userServiceDelegator.getUserById(user)).thenReturn(userDTO);
		when(webAppProperties.getUrl()).thenReturn(url);

		NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
		when(notificationProperties.getEmail()).thenReturn(notificationEmail);
		when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
		
		//invoke
		installationAccountOpeningNotifyOperatorService.execute(requestId);
		
		//verify
		verify(requestService, times(1)).findRequestById(requestId);
		verify(userServiceDelegator, times(1)).getUserById(user);
		verify(webAppProperties, times(1)).getUrl();
		
		final ArgumentCaptor<EmailData<EmailNotificationTemplateData>> recipientEmailCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailCaptor.capture(), Mockito.eq(email));
		//assert email argument
		EmailData<EmailNotificationTemplateData> emailData = recipientEmailCaptor.getValue();
		assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.ACCOUNT_APPLICATION_REJECTED.getName());
		assertThat(emailData.getNotificationTemplateData().getTemplateParams())
					.containsExactlyInAnyOrderEntriesOf(
							Map.of(
									PmrvEmailNotificationTemplateConstants.ACCOUNT_APPLICATION_REJECTED_REASON, rejectionReason,
									PmrvEmailNotificationTemplateConstants.HOME_URL, url,
									PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink
									));
	}
	
}
