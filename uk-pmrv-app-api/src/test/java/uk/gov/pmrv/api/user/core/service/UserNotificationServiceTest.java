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
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.token.JwtTokenService;
import uk.gov.pmrv.api.token.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.NavigationParams;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_FNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_LNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.OPERATOR_INVITATION_CONFIRMATION_LINK;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.USER_ROLE_TYPE;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.INVITATION_TO_EMITTER_CONTACT;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.RESET_2FA_CONFIRMATION;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.RESET_PASSWORD_CONFIRMATION;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.USER_ACCOUNT_ACTIVATION;
import static uk.gov.pmrv.api.user.NavigationOutcomes.OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    @InjectMocks
    private UserNotificationService userNotificationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private NotificationEmailService notificationEmailService;
    @Mock
    private JwtTokenService jwtTokenService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AppProperties appProperties;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NotificationProperties notificationProperties;

    @Test
    void notifyInvitedUser() {
        String token = "token";
        UserNotificationWithRedirectionLinkInfo notificationInfo = createNotificationInfo();
        UserNotificationWithRedirectionLinkInfo.TokenParams tokenParams = notificationInfo.getTokenParams();
        AppProperties.Web web = createAppWeb();

        when(appProperties.getWeb()).thenReturn(web);
        when(jwtTokenService
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval()))
            .thenReturn(token);

        userNotificationService.notifyUserWithLink(notificationInfo);

        verify(jwtTokenService, times(1))
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval());
        verify(appProperties, times(1)).getWeb();

        ArgumentCaptor<EmailData> emailInfoCaptor =
            ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(notificationInfo.getUserEmail()));

        EmailData emailInfo = emailInfoCaptor.getValue();

        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(notificationInfo.getTemplateName());
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                EmailNotificationTemplateConstants.APPLICANT_FNAME, "firstName",
                OPERATOR_INVITATION_CONFIRMATION_LINK, expectedNotificationLink(token, web)
            ));
        assertThat(emailInfo.getAttachments()).isEmpty();
    }

    @Test
    void notifyUserAccountActivation() {
        String userId = "userId";
        String roleName = "roleName";
        String email = "email";
        UserInfoDTO userInfo = UserInfoDTO.builder().email(email).build();
        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(USER_ACCOUNT_ACTIVATION)
                        .templateParams(Map.of(USER_ROLE_TYPE, roleName,
                                EmailNotificationTemplateConstants.CONTACT_REGULATOR, "link",
                                EmailNotificationTemplateConstants.HOME_URL, "url"))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);
        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");
        when(appProperties.getWeb().getUrl()).thenReturn("url");

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
        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(INVITATION_TO_EMITTER_CONTACT)
                        .templateParams(Map.of(APPLICANT_FNAME, userInfo.getFirstName(),
                                APPLICANT_LNAME, userInfo.getLastName(),
                                EmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                EmailNotificationTemplateConstants.CONTACT_REGULATOR, "link"))
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
        AppProperties.Web web = mock(AppProperties.Web.class);
        when(appProperties.getWeb()).thenReturn(web);
        when(web.getUrl()).thenReturn(url);
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
        
        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(RESET_PASSWORD_CONFIRMATION)
                        .templateParams(Map.of(
                        		EmailNotificationTemplateConstants.HOME_URL, url,
                        		EmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);

        userNotificationService.notifyUserPasswordReset(userId);

        verify(userAuthService, times(1)).getUserByUserId(userId);
        verify(appProperties, times(1)).getWeb();
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
        AppProperties.Web web = mock(AppProperties.Web.class);
        when(appProperties.getWeb()).thenReturn(web);
        when(web.getUrl()).thenReturn(url);
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
        
        EmailData emailInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(RESET_2FA_CONFIRMATION)
                        .templateParams(Map.of(
                        		EmailNotificationTemplateConstants.HOME_URL, url,
                        		EmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink))
                        .build())
                .build();

        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfo);

        userNotificationService.notifyUserReset2Fa(userId);

        verify(userAuthService, times(1)).getUserByUserId(userId);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfo, email);
    }

    private UserNotificationWithRedirectionLinkInfo createNotificationInfo() {
        return UserNotificationWithRedirectionLinkInfo.builder()
            .templateName(NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT)
            .userEmail("email")
            .linkParamName(OPERATOR_INVITATION_CONFIRMATION_LINK)
            .linkPath(OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
            .notificationParams(new HashMap<>(
                Map.of(EmailNotificationTemplateConstants.APPLICANT_FNAME, "firstName")))
            .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                .jwtTokenAction(JwtTokenActionEnum.OPERATOR_INVITATION)
                .claimValue("claimValue")
                .expirationInterval(5L)
                .build()
            )
            .build();
    }

    private String expectedNotificationLink(String token, AppProperties.Web web) {
        return UriComponentsBuilder
            .fromHttpUrl(web.getUrl())
            .path("/")
            .path(OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
            .queryParam(NavigationParams.TOKEN, token)
            .build()
            .toUriString();
    }

    private AppProperties.Web createAppWeb() {
        AppProperties.Web web = new AppProperties.Web();
        web.setUrl("http://www.pmrv.org.uk");
        return web;
    }
}