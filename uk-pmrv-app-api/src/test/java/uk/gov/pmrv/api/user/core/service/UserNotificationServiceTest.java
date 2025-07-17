package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.NavigationParams;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.user.NavigationOutcomes.OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    @InjectMocks
    private UserNotificationService userNotificationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
    @Mock
    private JwtTokenService jwtTokenService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebAppProperties webAppProperties;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NotificationProperties notificationProperties;

    @Test
    void notifyInvitedUser() {
        String token = "token";
        String url = "http://www.pmrv.org.uk";
        UserNotificationWithRedirectionLinkInfo notificationInfo = createNotificationInfo();
        UserNotificationWithRedirectionLinkInfo.TokenParams tokenParams = notificationInfo.getTokenParams();
        when(webAppProperties.getUrl()).thenReturn(url);

        when(jwtTokenService
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval()))
            .thenReturn(token);

        userNotificationService.notifyUserWithLink(notificationInfo);

        verify(jwtTokenService, times(1))
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval());
        verify(webAppProperties, times(1)).getUrl();

        ArgumentCaptor<EmailData<EmailNotificationTemplateData>> emailInfoCaptor =
            ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(notificationInfo.getUserEmail()));

        EmailData<EmailNotificationTemplateData> emailInfo = emailInfoCaptor.getValue();

        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(notificationInfo.getTemplateName().getName());
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
            		PmrvEmailNotificationTemplateConstants.APPLICANT_FNAME, "firstName",
            		PmrvEmailNotificationTemplateConstants.OPERATOR_INVITATION_CONFIRMATION_LINK, expectedNotificationLink(token, webAppProperties)
            ));
        assertThat(emailInfo.getAttachments()).isEmpty();
    }

    @Test
    void notifyUserAccountActivation() {
        String userId = "userId";
        String roleName = "roleName";
        String email = "email";
        UserInfoDTO userInfo = UserInfoDTO.builder().email(email).build();
        EmailData<EmailNotificationTemplateData> emailInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.USER_ACCOUNT_ACTIVATION.getName())
                        .templateParams(Map.of(PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, roleName,
                        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, "link",
                        		PmrvEmailNotificationTemplateConstants.HOME_URL, "url"))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);
        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");
        when(webAppProperties.getUrl()).thenReturn("url");

        userNotificationService.notifyUserAccountActivation(userId, roleName);

        verify(userAuthService, times(1)).getUserByUserId(userId);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfo, email);
    }

    @Test
    void notifyNewEmitterContact() {
        String userId = "userId";
        String installationName = "installationName";
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email").build();
        EmailData<EmailNotificationTemplateData> emailInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.INVITATION_TO_EMITTER_CONTACT.getName())
                        .templateParams(Map.of(PmrvEmailNotificationTemplateConstants.APPLICANT_FNAME, userInfo.getFirstName(),
                        		PmrvEmailNotificationTemplateConstants.APPLICANT_LNAME, userInfo.getLastName(),
                        		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, "link"))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);
        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");

        userNotificationService.notifyEmitterContactAccountActivation(userId, installationName);

        verify(userAuthService, times(1)).getUserByUserId(userId);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfo, userInfo.getEmail());
    }
    
    @Test
    void notifyUserPasswordReset() {
        String userId = "userId";
        String email = "email";
        String url = "home.com";
        String contactUsLink = url + "/contact-us";
        UserInfoDTO userInfo = UserInfoDTO.builder().email(email).build();
        when(webAppProperties.getUrl()).thenReturn(url);
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
        
        EmailData<EmailNotificationTemplateData> emailInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.RESET_PASSWORD_CONFIRMATION.getName())
                        .templateParams(Map.of(
                        		PmrvEmailNotificationTemplateConstants.HOME_URL, url,
                        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);

        userNotificationService.notifyUserPasswordReset(userId);

        verify(userAuthService, times(1)).getUserByUserId(userId);
        verify(webAppProperties, times(1)).getUrl();
        verify(notificationProperties, times(1)).getEmail();
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfo, email);
    }
    
    @Test
    void notifyUserReset2Fa() {
        String userId = "userId";
        String email = "email";
        String url = "home.com";
        String contactUsLink = url + "/contact-us";
        UserInfoDTO userInfo = UserInfoDTO.builder().email(email).build();
        when(webAppProperties.getUrl()).thenReturn(url);
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
        
        EmailData<EmailNotificationTemplateData> emailInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.RESET_2FA_CONFIRMATION.getName())
                        .templateParams(Map.of(
                        		PmrvEmailNotificationTemplateConstants.HOME_URL, url,
                        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);

        userNotificationService.notifyUserReset2Fa(userId);

        verify(userAuthService, times(1)).getUserByUserId(userId);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfo, email);
    }

    private UserNotificationWithRedirectionLinkInfo createNotificationInfo() {
        return UserNotificationWithRedirectionLinkInfo.builder()
            .templateName(PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT)
            .userEmail("email")
            .linkParamName(PmrvEmailNotificationTemplateConstants.OPERATOR_INVITATION_CONFIRMATION_LINK)
            .linkPath(OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
            .notificationParams(new HashMap<>(
                Map.of(PmrvEmailNotificationTemplateConstants.APPLICANT_FNAME, "firstName")))
            .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                .jwtTokenAction(JwtTokenAction.OPERATOR_INVITATION)
                .claimValue("claimValue")
                .expirationInterval(5L)
                .build()
            )
            .build();
    }

    private String expectedNotificationLink(String token, WebAppProperties web) {
        return UriComponentsBuilder
            .fromUriString(web.getUrl())
            .path("/")
            .path(OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
            .queryParam(NavigationParams.TOKEN, token)
            .build()
            .toUriString();
    }

}