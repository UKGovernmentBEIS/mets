package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInfoService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.OperatorRecallEmailNotificationHandler;

@Component
public class ALROperatorRecallEmailNotificationHandler extends OperatorRecallEmailNotificationHandler {

    private static final String ALR_APPLICATION_RECALLED_BY_OPERATOR = "ALR APPLICATION RECALLED BY OPERATOR";


    public ALROperatorRecallEmailNotificationHandler(UserAuthService userAuthService, AccountQueryService accountQueryService,
                                                     InstallationAccountQueryService installationAccountQueryService,
                                                     VerifierAuthorityQueryService verifierAuthorityQueryService,
                                                     VerifierUserInfoService verifierUserInfoService,
                                                     NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService) {
        super(userAuthService, accountQueryService, installationAccountQueryService, verifierAuthorityQueryService, verifierUserInfoService, notificationEmailService);
    }

    @Override
    public String getType() {
        return ALR_APPLICATION_RECALLED_BY_OPERATOR;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.ALR;
    }
}
