package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.AuthorityConstants;
import uk.gov.netz.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.netz.api.authorization.core.service.RoleService;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserNotificationGatewayTest {

    @InjectMocks
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Mock
    private RoleService roleService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private NotificationProperties notificationProperties;

    @Mock
    private JwtProperties jwtProperties;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebAppProperties webAppProperties;


    @Test
    void notifyInvitedUser() {
        String receiverEmail = "receiverEmail";
        String roleCode = "roleCode";
        String accountName = "accountName";
        String authorityUuid = "authorityUuid";
        String roleName = "roleName";
        RoleDTO roleDTO = RoleDTO.builder().code(roleCode).name(roleName).build();

        OperatorUserInvitationDTO operatorUserInvitationDTO =
            OperatorUserInvitationDTO
                .builder()
                .email(receiverEmail)
                .roleCode(roleCode)
                .build();

        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 60L;

        when(roleService.getRoleByCode(roleCode)).thenReturn(roleDTO);
        when(jwtProperties.getClaim()).thenReturn(claim);
        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");

        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        operatorUserNotificationGateway.notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);

        verify(roleService , times(1)).getRoleByCode(roleCode);

        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
            ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUserWithLink(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(operatorUserInvitationDTO.getEmail());
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(PmrvEmailNotificationTemplateConstants.OPERATOR_INVITATION_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL);
        assertThat(notificationInfo.getNotificationParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
            		PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, roleDTO.getName(),
            		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, accountName,
                    PmrvEmailNotificationTemplateConstants.EXPIRATION_MINUTES, 60L,
                    PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, "link"
            ));
        assertThat(notificationInfo.getTokenParams())
            .isEqualTo(expectedInvitationLinkTokenParams(JwtTokenAction.OPERATOR_INVITATION, authorityUuid, expirationInterval));

    }

    @Test
    void notifyRegisteredUser() {
        String contactUsLink = "/contact-us";
        OperatorUserDTO operatorUserDTO =
            OperatorUserDTO.builder().firstName("fn").lastName("ln").email("email").build();

        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);

        operatorUserNotificationGateway.notifyRegisteredUser(operatorUserDTO);

        //verify
        ArgumentCaptor<EmailData<EmailNotificationTemplateData>> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(operatorUserDTO.getEmail()));

        EmailData<EmailNotificationTemplateData> emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.USER_ACCOUNT_CREATED.getName());

        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
            		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_EMAIL, operatorUserDTO.getEmail(),
            		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink
            ));
    }

    @Test
    void notifyEmailVerification() {
        String email = "email";
        String contactUsLink = "/contact-us";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 10L;

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getUserInvitationExpIntervalMinutes()).thenReturn(expirationInterval);

        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);

        operatorUserNotificationGateway.notifyEmailVerification("email");

        //verify
        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
            ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUserWithLink(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.EMAIL_CONFIRMATION);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(email);
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(PmrvEmailNotificationTemplateConstants.EMAIL_CONFIRMATION_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.REGISTRATION_EMAIL_VERIFY_CONFIRMATION_URL);
        assertThat(notificationInfo.getNotificationParams())
                .isEqualTo(Map.of(PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink));
        assertThat(notificationInfo.getTokenParams())
            .isEqualTo(expectedInvitationLinkTokenParams(JwtTokenAction.USER_REGISTRATION, email, expirationInterval));
    }

    @Test
    void notifyInviteeAcceptedInvitation() {
    	UserInfoDTO inviteeUser = UserInfoDTO.builder()
            .firstName("firstName")
            .lastName("lastName")
            .email("email")
            .build();

        when(notificationProperties.getEmail().getContactUsLink()).thenReturn("link");
        when(webAppProperties.getUrl()).thenReturn("url");

        operatorUserNotificationGateway.notifyInviteeAcceptedInvitation(inviteeUser);

        //verify
        ArgumentCaptor<EmailData<EmailNotificationTemplateData>> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(inviteeUser.getEmail()));

        EmailData<EmailNotificationTemplateData> emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.INVITEE_INVITATION_ACCEPTED.getName());
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(
            Map.of(PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, "Operator",
            		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, "link",
            		PmrvEmailNotificationTemplateConstants.HOME_URL, "url")
        );
    }

    @Test
    void notifyInviterAcceptedInvitation() {
    	UserInfoDTO inviteeUser = UserInfoDTO.builder()
                .firstName("inviteeName")
                .lastName("inviteeLastName")
                .email("inviteeEmail")
                .build();
        UserInfoDTO inviterUser =  UserInfoDTO.builder()
                .firstName("inviterName")
                .lastName("inviterLastName")
                .email("inviterEmail")
                .build();

        operatorUserNotificationGateway.notifyInviterAcceptedInvitation(inviteeUser, inviterUser);

        //verify
        ArgumentCaptor<EmailData<EmailNotificationTemplateData>> emailInfoCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailInfoCaptor.capture(), Mockito.eq(inviterUser.getEmail()));

        EmailData<EmailNotificationTemplateData> emailInfo = emailInfoCaptor.getValue();
        assertThat(emailInfo.getNotificationTemplateData().getTemplateName()).isEqualTo(PmrvNotificationTemplateName.INVITER_INVITATION_ACCEPTED.getName());
        assertThat(emailInfo.getNotificationTemplateData().getTemplateParams()).containsExactlyInAnyOrderEntriesOf(Map.of(
        		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME, inviterUser.getFirstName(),
        		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME, inviterUser.getLastName(),
        		PmrvEmailNotificationTemplateConstants.USER_INVITEE_FNAME, inviteeUser.getFirstName(),
        		PmrvEmailNotificationTemplateConstants.USER_INVITEE_LNAME, inviteeUser.getLastName()
        ));
    }

    @Test
    void notifyUsersUpdateStatus() {
        Long accountId = 1L;
        String installationName = "installationName";

        NewUserActivated operator1 = NewUserActivated.builder().userId("operator1").roleCode(AuthorityConstants.OPERATOR_ROLE_CODE).build();
        NewUserActivated operator2 = NewUserActivated.builder().userId("operator2").roleCode(AuthorityConstants.OPERATOR_ROLE_CODE).build();
        NewUserActivated emitter1 = NewUserActivated.builder().userId("emitter1").accountId(accountId)
                .roleCode(AuthorityConstants.EMITTER_CONTACT).build();
        NewUserActivated emitter2 = NewUserActivated.builder().userId("emitter2").accountId(accountId)
                .roleCode(AuthorityConstants.EMITTER_CONTACT).build();

        List<NewUserActivated> activatedOperators = List.of(operator1, operator2, emitter1, emitter2);

        when(accountQueryService.getAccountName(accountId)).thenReturn(installationName);

        // Invoke
        operatorUserNotificationGateway.notifyUsersUpdateStatus(activatedOperators);

        // Verify
        verify(userNotificationService, times(1))
                .notifyEmitterContactAccountActivation(emitter1.getUserId(), installationName);
        verify(userNotificationService, times(1))
                .notifyEmitterContactAccountActivation(emitter2.getUserId(), installationName);
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(operator1.getUserId(), "Operator");
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(operator2.getUserId(), "Operator");
        verify(accountQueryService, times(2))
                .getAccountName(accountId);
        verifyNoMoreInteractions(userNotificationService, accountQueryService);
    }

    @Test
    void notifyUsersUpdateStatus_with_exception() {
        Long accountId = 1L;
        String installationName = "installationName";

        NewUserActivated operator1 = NewUserActivated.builder().userId("operator1").roleCode(AuthorityConstants.OPERATOR_ROLE_CODE).build();
        NewUserActivated operator2 = NewUserActivated.builder().userId("operator2").roleCode(AuthorityConstants.OPERATOR_ROLE_CODE).build();
        NewUserActivated emitter1 = NewUserActivated.builder().userId("emitter1").accountId(accountId)
                .roleCode(AuthorityConstants.EMITTER_CONTACT).build();

        List<NewUserActivated> activatedOperators = List.of(emitter1, operator1, operator2);

        when(accountQueryService.getAccountName(accountId))
                .thenThrow(NullPointerException.class);

        // Invoke
        operatorUserNotificationGateway.notifyUsersUpdateStatus(activatedOperators);

        // Verify
        verify(userNotificationService, never())
                .notifyEmitterContactAccountActivation(emitter1.getUserId(), installationName);
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(operator1.getUserId(), "Operator");
        verify(userNotificationService, times(1))
                .notifyUserAccountActivation(operator2.getUserId(), "Operator");
        verify(accountQueryService, times(1))
                .getAccountName(accountId);
        verifyNoMoreInteractions(userNotificationService, accountQueryService);
    }

    private UserNotificationWithRedirectionLinkInfo.TokenParams expectedInvitationLinkTokenParams(JwtTokenAction jwtTokenAction,
                                                                                                  String claimValue, long expirationInterval) {
        return UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
            .jwtTokenAction(jwtTokenAction)
            .claimValue(claimValue)
            .expirationInterval(expirationInterval)
            .build();
    }
}