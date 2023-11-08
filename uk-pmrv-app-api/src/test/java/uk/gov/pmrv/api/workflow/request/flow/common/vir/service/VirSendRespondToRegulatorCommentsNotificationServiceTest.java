package uk.gov.pmrv.api.workflow.request.flow.common.vir.service;

import static org.mockito.Mockito.mock;
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
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;

@ExtendWith(MockitoExtension.class)
class VirSendRespondToRegulatorCommentsNotificationServiceTest {

    @InjectMocks
    private VirRespondToRegulatorCommentsNotificationService service;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private AccountCaSiteContactService accountCaSiteContactService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private AppProperties appProperties;

    private static final String homeUrl = "url";

    @Test
    void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator() {
        final long accountId = 1L;

        final String reviewer = "reviewer";
        final String reviewerEmail = "regulator@test.gr";
        final UserInfoDTO reviewerUser = UserInfoDTO.builder().userId(reviewer).email(reviewerEmail).build();

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.VIR)
                .payload(VirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        final String siteContact = "siteContact";
        final String siteContactEmail = "regulator2@test.gr";
        final UserInfoDTO siteContactUser = UserInfoDTO.builder().userId(siteContact).email(siteContactEmail).build();

        final String installationName = "Installation Name";
        final String emitterId = "emitterId";
        final AccountInfoDTO accountInfoDTO = AccountInfoDTO.builder()
                .name(installationName)
                .emitterId(emitterId)
                .build();
        final EmailData notifyInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(NotificationTemplateName.VIR_NOTIFICATION_OPERATOR_RESPONSE)
                        .templateParams(Map.of(
                                EmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                EmailNotificationTemplateConstants.EMITTER_ID, emitterId,
                                EmailNotificationTemplateConstants.HOME_URL, homeUrl
                        ))
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of(reviewer, AuthorityStatus.ACTIVE));
        when(userAuthService.getUserByUserId(reviewer)).thenReturn(reviewerUser);
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.of(siteContact));
        when(userAuthService.getUserByUserId(siteContact)).thenReturn(siteContactUser);
        when(accountQueryService.getAccountInfoDTOById(accountId)).thenReturn(accountInfoDTO);
        when(appProperties.getWeb()).thenReturn(mock(AppProperties.Web.class));
        when(appProperties.getWeb().getUrl()).thenReturn(homeUrl);

        // Invoke
        service.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(userAuthService, times(1)).getUserByUserId(reviewer);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verify(userAuthService, times(1)).getUserByUserId(siteContact);
        verify(accountQueryService, times(1)).getAccountInfoDTOById(accountId);
        verify(notificationEmailService, times(1))
                .notifyRecipients(notifyInfo, List.of(reviewerEmail, siteContactEmail), List.of());
    }

    @Test
    void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator_only_reviewer() {
        final long accountId = 1L;

        final String reviewer = "reviewer";
        final String reviewerEmail = "regulator@test.gr";
        final UserInfoDTO reviewerUser = UserInfoDTO.builder().userId(reviewer).email(reviewerEmail).build();

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.VIR)
                .payload(VirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        final String installationName = "Installation Name";
        final String emitterId = "emitterId";
        final AccountInfoDTO accountInfoDTO = AccountInfoDTO.builder()
            .name(installationName)
            .emitterId(emitterId)
            .build();
        final EmailData notifyInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(NotificationTemplateName.VIR_NOTIFICATION_OPERATOR_RESPONSE)
                        .templateParams(Map.of(
                                EmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                EmailNotificationTemplateConstants.EMITTER_ID, emitterId,
                                EmailNotificationTemplateConstants.HOME_URL, homeUrl
                        ))
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of(reviewer, AuthorityStatus.ACTIVE));
        when(userAuthService.getUserByUserId(reviewer)).thenReturn(reviewerUser);
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.empty());
        when(accountQueryService.getAccountInfoDTOById(accountId)).thenReturn(accountInfoDTO);
        when(appProperties.getWeb()).thenReturn(mock(AppProperties.Web.class));
        when(appProperties.getWeb().getUrl()).thenReturn(homeUrl);

        // Invoke
        service.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(userAuthService, times(1)).getUserByUserId(reviewer);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verify(accountQueryService, times(1)).getAccountInfoDTOById(accountId);
        verify(notificationEmailService, times(1))
                .notifyRecipients(notifyInfo, List.of(reviewerEmail), List.of());
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
                .type(RequestType.VIR)
                .payload(VirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        final String installationName = "Installation Name";
        final String emitterId = "emitterId";
        final AccountInfoDTO accountInfoDTO = AccountInfoDTO.builder()
            .name(installationName)
            .emitterId(emitterId)
            .build();
        final EmailData notifyInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(NotificationTemplateName.VIR_NOTIFICATION_OPERATOR_RESPONSE)
                        .templateParams(Map.of(
                                EmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                EmailNotificationTemplateConstants.EMITTER_ID, emitterId,
                                EmailNotificationTemplateConstants.HOME_URL, homeUrl
                        ))
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of(reviewer, AuthorityStatus.DISABLED));
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.of(siteContact));
        when(userAuthService.getUserByUserId(siteContact)).thenReturn(siteContactUser);
        when(accountQueryService.getAccountInfoDTOById(accountId)).thenReturn(accountInfoDTO);
        when(appProperties.getWeb()).thenReturn(mock(AppProperties.Web.class));
        when(appProperties.getWeb().getUrl()).thenReturn(homeUrl);

        // Invoke
        service.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verify(userAuthService, times(1)).getUserByUserId(siteContact);
        verify(accountQueryService, times(1)).getAccountInfoDTOById(accountId);
        verify(notificationEmailService, times(1))
                .notifyRecipients(notifyInfo, List.of(siteContactEmail), List.of());
        verifyNoMoreInteractions(userAuthService);
    }

    @Test
    void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator_no_recipients() {
        final long accountId = 1L;
        final String reviewer = "reviewer";

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.VIR)
                .payload(VirRequestPayload.builder()
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
        verifyNoInteractions(userAuthService, accountQueryService, notificationEmailService, appProperties);
    }

    @Test
    void sendDeadlineResponseToRegulatorCommentsNotificationToRegulator() {
        final long accountId = 1L;

        final String reviewer = "reviewer";
        final String reviewerEmail = "regulator@test.gr";
        final UserInfoDTO reviewerUser = UserInfoDTO.builder().userId(reviewer).email(reviewerEmail).build();

        final Request request = Request.builder()
                .accountId(accountId)
                .type(RequestType.VIR)
                .payload(VirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        final String siteContact = "siteContact";
        final String siteContactEmail = "regulator2@test.gr";
        final UserInfoDTO siteContactUser = UserInfoDTO.builder().userId(siteContact).email(siteContactEmail).build();

        final String installationName = "Installation Name";
        final String emitterId = "emitterId";
        final AccountInfoDTO accountInfoDTO = AccountInfoDTO.builder()
            .name(installationName)
            .emitterId(emitterId)
            .build();
        final EmailData notifyInfo = EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(NotificationTemplateName.VIR_NOTIFICATION_OPERATOR_MISSES_DEADLINE)
                        .templateParams(Map.of(
                                EmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
                                EmailNotificationTemplateConstants.EMITTER_ID, emitterId,
                                EmailNotificationTemplateConstants.HOME_URL, homeUrl
                        ))
                        .build())
                .build();

        when(authorityService.findStatusByUsers(List.of(reviewer))).thenReturn(Map.of(reviewer, AuthorityStatus.ACTIVE));
        when(userAuthService.getUserByUserId(reviewer)).thenReturn(reviewerUser);
        when(accountCaSiteContactService.findCASiteContactByAccount(accountId)).thenReturn(Optional.of(siteContact));
        when(userAuthService.getUserByUserId(siteContact)).thenReturn(siteContactUser);
        when(accountQueryService.getAccountInfoDTOById(accountId)).thenReturn(accountInfoDTO);
        when(appProperties.getWeb()).thenReturn(mock(AppProperties.Web.class));
        when(appProperties.getWeb().getUrl()).thenReturn(homeUrl);

        // Invoke
        service.sendDeadlineResponseToRegulatorCommentsNotificationToRegulator(request);

        // Verify
        verify(authorityService, times(1)).findStatusByUsers(List.of(reviewer));
        verify(userAuthService, times(1)).getUserByUserId(reviewer);
        verify(accountCaSiteContactService, times(1)).findCASiteContactByAccount(accountId);
        verify(userAuthService, times(1)).getUserByUserId(siteContact);
        verify(accountQueryService, times(1)).getAccountInfoDTOById(accountId);
        verify(notificationEmailService, times(1))
                .notifyRecipients(notifyInfo, List.of(reviewerEmail, siteContactEmail), List.of());
    }
}
