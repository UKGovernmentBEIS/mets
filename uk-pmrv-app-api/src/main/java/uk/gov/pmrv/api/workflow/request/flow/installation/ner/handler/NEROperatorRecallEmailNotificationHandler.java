package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
public class NEROperatorRecallEmailNotificationHandler extends OperatorRecallEmailNotificationHandler {

    private static final String NER_APPLICATION_RECALLED_BY_OPERATOR = "NER APPLICATION RECALLED BY OPERATOR";


    public NEROperatorRecallEmailNotificationHandler(UserAuthService userAuthService, AccountQueryService accountQueryService,
                                                     InstallationAccountQueryService installationAccountQueryService,
                                                     VerifierAuthorityQueryService verifierAuthorityQueryService,
                                                     VerifierUserInfoService verifierUserInfoService,
                                                     NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService) {
        super(userAuthService, accountQueryService, installationAccountQueryService, verifierAuthorityQueryService, verifierUserInfoService, notificationEmailService);
    }

    @Override
    public String getType() {
        return NER_APPLICATION_RECALLED_BY_OPERATOR;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.NER;
    }
}
