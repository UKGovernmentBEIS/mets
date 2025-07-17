package uk.gov.pmrv.api.user.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierUserNotificationGatewayTest {

    @InjectMocks
    private VerifierUserNotificationGateway verifierUserNotificationGateway;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebAppProperties webAppProperties;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NotificationProperties notificationProperties;

    @Test
    void notifyInvitedUser() {
        VerifierUserInvitationDTO verifierUserInvitation = createVerifierUserInvitationDTO();
        String authorityUuid = "uuid";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 60L;

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");

        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        // Invoke
        verifierUserNotificationGateway.notifyInvitedUser(verifierUserInvitation, authorityUuid);

        // Verify
        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
                ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUserWithLink(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.INVITATION_TO_VERIFIER_ACCOUNT);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(verifierUserInvitation.getEmail());
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(PmrvEmailNotificationTemplateConstants.VERIFIER_INVITATION_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.VERIFIER_REGISTRATION_INVITATION_ACCEPTED_URL);
        assertThat(notificationInfo.getNotificationParams()).containsExactlyInAnyOrderEntriesOf(
                Map.of(
                		PmrvEmailNotificationTemplateConstants.APPLICANT_FNAME, verifierUserInvitation.getFirstName(),
                		PmrvEmailNotificationTemplateConstants.APPLICANT_LNAME, verifierUserInvitation.getLastName(),
                        PmrvEmailNotificationTemplateConstants.EXPIRATION_MINUTES, 60L,
                        PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, "link"
                ));
        assertThat(notificationInfo.getTokenParams()).isEqualTo(expectedInvitationLinkTokenParams(authorityUuid, expirationInterval));
    }

    @Test
    void notifyInviteeAcceptedInvitation() {
        UserInfoDTO invitee = UserInfoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .build();

        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");
        when(webAppProperties.getUrl()).thenReturn("url");

        // Invoke
        verifierUserNotificationGateway.notifyInviteeAcceptedInvitation(invitee);

        // Verify
        ArgumentCaptor<EmailData<EmailNotificationTemplateData>> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(invitee.getEmail()));

        EmailData<EmailNotificationTemplateData> emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.INVITEE_INVITATION_ACCEPTED.getName());
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
                Map.of(PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, "Verifier",
                		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, "link",
                        PmrvEmailNotificationTemplateConstants.HOME_URL, "url")
        );
    }

    @Test
    void notifyInviterAcceptedInvitation() {
        UserInfoDTO invitee = UserInfoDTO.builder()
                .firstName("inviteeName")
                .lastName("inviteeLastName")
                .email("inviteeEmail")
                .build();
        UserInfoDTO inviter =  UserInfoDTO.builder()
                .firstName("inviterName")
                .lastName("inviterLastName")
                .email("inviterEmail")
                .build();

        // Invoke
        verifierUserNotificationGateway.notifyInviterAcceptedInvitation(invitee, inviter);

        // Verify
        ArgumentCaptor<EmailData<EmailNotificationTemplateData>> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(inviter.getEmail()));

        EmailData<EmailNotificationTemplateData> emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.INVITER_INVITATION_ACCEPTED.getName());
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(Map.of(
        		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME, inviter.getFirstName(),
        		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME, inviter.getLastName(),
        		PmrvEmailNotificationTemplateConstants.USER_INVITEE_FNAME, invitee.getFirstName(),
        		PmrvEmailNotificationTemplateConstants.USER_INVITEE_LNAME, invitee.getLastName()
        ));
    }

    @Test
    void activatedVerifiers() {
        String userId1 = "verifier1";
        String userId2 = "verifier2";
        List<String> notifications = List.of(userId1, userId2);

        // Invoke
        verifierUserNotificationGateway.notifyUsersUpdateStatus(notifications);

        // Verify
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(userId1, "Verifier");
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(userId2, "Verifier");
        verifyNoMoreInteractions(userNotificationService);
    }

    @Test
    void activatedVerifiers_with_exception() {
        String userId1 = "verifier1";
        String userId2 = "verifier2";
        List<String> notifications = List.of(userId1, userId2);

        doThrow(new RuntimeException())
                .when(userNotificationService)
                .notifyUserAccountActivation(userId1, "Verifier");

        // Invoke
        verifierUserNotificationGateway.notifyUsersUpdateStatus(notifications);

        // Verify
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(userId1, "Verifier");
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(userId2, "Verifier");
        verifyNoMoreInteractions(userNotificationService);
    }

    private VerifierUserInvitationDTO createVerifierUserInvitationDTO() {
        return VerifierUserInvitationDTO.builder()
                .roleCode("roleCode")
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .phoneNumber("69999999999")
                .build();
    }

    private UserNotificationWithRedirectionLinkInfo.TokenParams expectedInvitationLinkTokenParams(String authUuid,
                                                                                                  long expirationInterval) {
        return UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                .jwtTokenAction(JwtTokenAction.VERIFIER_INVITATION)
                .claimValue(authUuid)
                .expirationInterval(expirationInterval)
                .build();
    }

}