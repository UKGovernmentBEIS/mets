package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInfoService;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NEROperatorRecallEmailNotificationHandlerTest {

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private VerifierAuthorityQueryService verifierAuthorityQueryService;

    @Mock
    private VerifierUserInfoService verifierUserInfoService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    private NEROperatorRecallEmailNotificationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new NEROperatorRecallEmailNotificationHandler(
                userAuthService,
                accountQueryService,
                installationAccountQueryService,
                verifierAuthorityQueryService,
                verifierUserInfoService,
                notificationEmailService
        );
    }

    @Test
    void getType() {
        assertThat(handler.getType())
                .isEqualTo("NER APPLICATION RECALLED BY OPERATOR");
    }
}
