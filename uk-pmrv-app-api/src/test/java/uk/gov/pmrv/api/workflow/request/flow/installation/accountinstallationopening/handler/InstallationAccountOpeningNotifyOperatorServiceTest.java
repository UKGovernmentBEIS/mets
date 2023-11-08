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
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.application.UserService;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
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
	private NotificationEmailService notificationEmailService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private RequestService requestService;

	@Mock
	private AppProperties appProperties;

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
		final ApplicationUserDTO userDTO = OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();
		final AppProperties.Web web = new AppProperties.Web();
		web.setUrl("some-url");
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(userService.getUserById(user)).thenReturn(userDTO);
		when(appProperties.getWeb()).thenReturn(web);

		NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
		when(notificationProperties.getEmail()).thenReturn(notificationEmail);
		when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
		
		//invoke
		installationAccountOpeningNotifyOperatorService.execute(requestId);
		
		//verify
		verify(requestService, times(1)).findRequestById(requestId);
		verify(userService, times(1)).getUserById(user);
		verify(appProperties, times(1)).getWeb();
		
		final ArgumentCaptor<EmailData> recipientEmailCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailCaptor.capture(), Mockito.eq(email));
		//assert email argument
		EmailData emailData = recipientEmailCaptor.getValue();
		assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.ACCOUNT_APPLICATION_ACCEPTED);
		assertThat(emailData.getNotificationTemplateData().getTemplateParams())
					.containsExactlyInAnyOrderEntriesOf(
							Map.of(
									EmailNotificationTemplateConstants.HOME_URL, web.getUrl(),
									EmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink
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
		final ApplicationUserDTO userDTO = OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();
		final AppProperties.Web web = new AppProperties.Web();
		web.setUrl("some-url");
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(userService.getUserById(user)).thenReturn(userDTO);
		when(appProperties.getWeb()).thenReturn(web);

		NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
		when(notificationProperties.getEmail()).thenReturn(notificationEmail);
		when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
		
		//invoke
		installationAccountOpeningNotifyOperatorService.execute(requestId);
		
		//verify
		verify(requestService, times(1)).findRequestById(requestId);
		verify(userService, times(1)).getUserById(user);
		verify(appProperties, times(1)).getWeb();
		
		final ArgumentCaptor<EmailData> recipientEmailCaptor = ArgumentCaptor.forClass(EmailData.class);
		verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailCaptor.capture(), Mockito.eq(email));
		//assert email argument
		EmailData emailData = recipientEmailCaptor.getValue();
		assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.ACCOUNT_APPLICATION_REJECTED);
		assertThat(emailData.getNotificationTemplateData().getTemplateParams())
					.containsExactlyInAnyOrderEntriesOf(
							Map.of(
									EmailNotificationTemplateConstants.ACCOUNT_APPLICATION_REJECTED_REASON, rejectionReason,
									EmailNotificationTemplateConstants.HOME_URL, web.getUrl(),
									EmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink
									));
	}
	
}
