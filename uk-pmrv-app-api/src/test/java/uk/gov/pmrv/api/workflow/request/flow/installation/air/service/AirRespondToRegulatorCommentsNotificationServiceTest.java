package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;

@ExtendWith(MockitoExtension.class)
class AirRespondToRegulatorCommentsNotificationServiceTest {

    @InjectMocks
    private AirRespondToRegulatorCommentsNotificationService service;

    @Mock
    private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private AccountCaSiteContactService accountCaSiteContactService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private WebAppProperties webAppProperties;

    private static final String homeUrl = "url";

    @Test
    void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator() {
        
        final long accountId = 1L;

        final String reviewer = "reviewer";
        final String reviewerEmail = "regulator@test.gr";
        final UserInfoDTO reviewerUser = UserInfoDTO.builder().userId(reviewer).email(reviewerEmail).build();

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.AIR)
                .payload(AirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        final String siteContact = "siteContact";
        final String siteContactEmail = "regulator2@test.gr";
        final UserInfoDTO siteContactUser = UserInfoDTO.builder().userId(siteContact).email(siteContactEmail).build();

        final String installationName = "Installation Name";
        final String emitterId = "emitterId";
        final InstallationOperatorDetails operatorDetails = InstallationOperatorDetails.builder()
                .installationName(installationName)
                .emitterId(emitterId)
                .build();
        final EmailData<EmailNotificationTemplateData> notifyInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.AIR_NOTIFICATION_OPERATOR_RESPONSE.getName())
                        .templateParams(Map.of(
                        		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                PmrvEmailNotificationTemplateConstants.EMITTER_ID, emitterId,
                                PmrvEmailNotificationTemplateConstants.HOME_URL, homeUrl
                        ))
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of(reviewer, AuthorityStatus.ACTIVE));
        when(userAuthService.getUserByUserId(reviewer)).thenReturn(reviewerUser);
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.of(siteContact));
        when(userAuthService.getUserByUserId(siteContact)).thenReturn(siteContactUser);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(operatorDetails);
        when(webAppProperties.getUrl()).thenReturn(homeUrl);

        // Invoke
        service.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(userAuthService, times(1)).getUserByUserId(reviewer);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verify(userAuthService, times(1)).getUserByUserId(siteContact);
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(notificationEmailService, times(1))
                .notifyRecipients(notifyInfo, List.of(reviewerEmail, siteContactEmail));
    }

    @Test
    void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator_only_reviewer() {
        
        final long accountId = 1L;

        final String reviewer = "reviewer";
        final String reviewerEmail = "regulator@test.gr";
        final UserInfoDTO reviewerUser = UserInfoDTO.builder().userId(reviewer).email(reviewerEmail).build();

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.AIR)
                .payload(AirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        final String installationName = "Installation Name";
        final String emitterId = "emitterId";
        final InstallationOperatorDetails operatorDetails = InstallationOperatorDetails.builder()
                .installationName(installationName)
                .emitterId(emitterId)
                .build();
        final EmailData<EmailNotificationTemplateData> notifyInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.AIR_NOTIFICATION_OPERATOR_RESPONSE.getName())
                        .templateParams(Map.of(
                        		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                PmrvEmailNotificationTemplateConstants.EMITTER_ID, emitterId,
                                PmrvEmailNotificationTemplateConstants.HOME_URL, homeUrl
                        ))
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of(reviewer, AuthorityStatus.ACTIVE));
        when(userAuthService.getUserByUserId(reviewer)).thenReturn(reviewerUser);
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.empty());
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(operatorDetails);
        when(webAppProperties.getUrl()).thenReturn(homeUrl);

        // Invoke
        service.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(userAuthService, times(1)).getUserByUserId(reviewer);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(notificationEmailService, times(1))
                .notifyRecipients(notifyInfo, List.of(reviewerEmail));
        verifyNoMoreInteractions(userAuthService);
    }

    @Test
    void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator_only_site_contact() {
        final long accountId = 1L;

        final String reviewer = "reviewer";

        final String siteContact = "siteContact";
        final String siteContactEmail = "regulator2@test.gr";
        final UserInfoDTO siteContactUser = UserInfoDTO.builder().userId(siteContact).email(siteContactEmail).build();

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.AIR)
                .payload(AirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        final String installationName = "Installation Name";
        final String emitterId = "emitterId";
        final InstallationOperatorDetails operatorDetails = InstallationOperatorDetails.builder()
                .installationName(installationName)
                .emitterId(emitterId)
                .build();
        final EmailData<EmailNotificationTemplateData> notifyInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.AIR_NOTIFICATION_OPERATOR_RESPONSE.getName())
                        .templateParams(Map.of(
                        		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                PmrvEmailNotificationTemplateConstants.EMITTER_ID, emitterId,
                                PmrvEmailNotificationTemplateConstants.HOME_URL, homeUrl
                        ))
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of(reviewer, AuthorityStatus.DISABLED));
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.of(siteContact));
        when(userAuthService.getUserByUserId(siteContact)).thenReturn(siteContactUser);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(operatorDetails);
        when(webAppProperties.getUrl()).thenReturn(homeUrl);

        // Invoke
        service.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verify(userAuthService, times(1)).getUserByUserId(siteContact);
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(notificationEmailService, times(1))
                .notifyRecipients(notifyInfo, List.of(siteContactEmail));
        verifyNoMoreInteractions(userAuthService);
    }

    @Test
    void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator_no_recipients() {
        final long accountId = 1L;
        final String reviewer = "reviewer";

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.AIR)
                .payload(AirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of());
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.empty());

        // Invoke
        service.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verifyNoInteractions(userAuthService, installationOperatorDetailsQueryService, notificationEmailService, webAppProperties);
    }

    @Test
    void sendDeadlineResponseToRegulatorCommentsNotificationToRegulator() {
        final long accountId = 1L;

        final String reviewer = "reviewer";
        final String reviewerEmail = "regulator@test.gr";
        final UserInfoDTO reviewerUser = UserInfoDTO.builder().userId(reviewer).email(reviewerEmail).build();

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.AIR)
                .payload(AirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        final String siteContact = "siteContact";
        final String siteContactEmail = "regulator2@test.gr";
        final UserInfoDTO siteContactUser = UserInfoDTO.builder().userId(siteContact).email(siteContactEmail).build();

        final String installationName = "Installation Name";
        final String emitterId = "emitterId";
        final InstallationOperatorDetails operatorDetails = InstallationOperatorDetails.builder()
                .installationName(installationName)
                .emitterId(emitterId)
                .build();
        final EmailData<EmailNotificationTemplateData> notifyInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.AIR_NOTIFICATION_OPERATOR_MISSES_DEADLINE.getName())
                        .templateParams(Map.of(
                        		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                PmrvEmailNotificationTemplateConstants.EMITTER_ID, emitterId,
                                PmrvEmailNotificationTemplateConstants.HOME_URL, homeUrl
                        ))
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of(reviewer, AuthorityStatus.ACTIVE));
        when(userAuthService.getUserByUserId(reviewer)).thenReturn(reviewerUser);
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.of(siteContact));
        when(userAuthService.getUserByUserId(siteContact)).thenReturn(siteContactUser);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(operatorDetails);
        when(webAppProperties.getUrl()).thenReturn(homeUrl);

        // Invoke
        service.sendDeadlineResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(userAuthService, times(1)).getUserByUserId(reviewer);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verify(userAuthService, times(1)).getUserByUserId(siteContact);
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(notificationEmailService, times(1))
                .notifyRecipients(notifyInfo, List.of(reviewerEmail, siteContactEmail));
    }
}
