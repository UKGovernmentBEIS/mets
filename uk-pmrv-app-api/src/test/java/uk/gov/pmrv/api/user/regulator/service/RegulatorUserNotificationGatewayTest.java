package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.token.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.pmrv.api.token.JwtProperties;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo.TokenParams;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;

@ExtendWith(MockitoExtension.class)
class RegulatorUserNotificationGatewayTest {

    @InjectMocks
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;

    @Mock
    private UserNotificationService userNotificationService;
    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private JwtProperties jwtProperties;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NotificationProperties notificationProperties;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AppProperties appProperties;

    @Test
    void notifyInvitedUser() {
        RegulatorInvitedUserDTO regulatorInvitedUser = createInvitedUser();
        RegulatorInvitedUserDetailsDTO userDetails = regulatorInvitedUser.getUserDetails();
        String authorityUuid = "uuid";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 60L;

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(jwtProperties.getClaim()).thenReturn(claim);
        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");

        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        //invoke
        regulatorUserNotificationGateway.notifyInvitedUser(regulatorInvitedUser.getUserDetails(), authorityUuid);

        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
                ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUserWithLink(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(NotificationTemplateName.INVITATION_TO_REGULATOR_ACCOUNT);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(userDetails.getEmail());
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(EmailNotificationTemplateConstants.REGULATOR_INVITATION_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.REGULATOR_REGISTRATION_INVITATION_ACCEPTED_URL);
        assertThat(notificationInfo.getNotificationParams()).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        EmailNotificationTemplateConstants.APPLICANT_FNAME, userDetails.getFirstName(),
                        EmailNotificationTemplateConstants.APPLICANT_LNAME, userDetails.getLastName(),
                        EmailNotificationTemplateConstants.EXPIRATION_MINUTES, 60L,
                        EmailNotificationTemplateConstants.CONTACT_REGULATOR, "link"
                ));
        assertThat(notificationInfo.getTokenParams()).isEqualTo(expectedInvitationLinkTokenParams(authorityUuid, expirationInterval));
    }

    @Test
    void notifyInviteeAcceptedInvitation() {
        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");
        when(appProperties.getWeb().getUrl()).thenReturn("url");

        //invoke
        regulatorUserNotificationGateway.notifyInviteeAcceptedInvitation("email");

        ArgumentCaptor<EmailData> emailDataCaptor =
                ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), eq("email"));

        EmailData emailData = emailDataCaptor.getValue();

        assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(NotificationTemplateName.INVITEE_INVITATION_ACCEPTED);
        assertThat(emailData.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                        EmailNotificationTemplateConstants.USER_ROLE_TYPE, "Regulator",
                        EmailNotificationTemplateConstants.CONTACT_REGULATOR, "link",
                        EmailNotificationTemplateConstants.HOME_URL, "url"
                ));
    }

    @Test
    void sendActivationNotifications_whenActivations_thenNotifications() {
        regulatorUserNotificationGateway.sendUpdateNotifications(List.of("user2"));

        verify(userNotificationService, times(1)).notifyUserAccountActivation("user2", "Regulator");
    }

    private RegulatorInvitedUserDTO createInvitedUser() {
        return RegulatorInvitedUserDTO.builder()
                .userDetails(RegulatorInvitedUserDetailsDTO.builder()
                        .firstName("fn")
                        .lastName("ln")
                        .email("em@em.gr")
                        .jobTitle("title")
                        .phoneNumber("210000")
                        .build()
                )
                .permissions(Map.of(MANAGE_USERS_AND_CONTACTS, NONE))
                .build();
    }

    private TokenParams expectedInvitationLinkTokenParams(String authUuid, long expirationInterval) {
        return TokenParams.builder()
                .jwtTokenAction(JwtTokenActionEnum.REGULATOR_INVITATION)
                .claimValue(authUuid)
                .expirationInterval(expirationInterval)
                .build();
    }
}
