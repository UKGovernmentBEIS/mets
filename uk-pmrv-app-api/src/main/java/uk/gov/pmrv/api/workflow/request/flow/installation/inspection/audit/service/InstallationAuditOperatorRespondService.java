package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionOperatorRespondService;

@Service
public class InstallationAuditOperatorRespondService
        extends InstallationInspectionOperatorRespondService {

    public InstallationAuditOperatorRespondService(
            RequestService requestService,
            AuthorityService authorityService,
            AccountCaSiteContactService accountCaSiteContactService,
            UserAuthService userAuthService,
            InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService,
            NotificationEmailService<EmailNotificationTemplateData> notificationEmailService, WebAppProperties webAppProperties) {
        super(requestService,
                authorityService,
                userAuthService,
                installationOperatorDetailsQueryService,
                notificationEmailService,
                webAppProperties);
    }

    @Override
    protected RequestActionType getOperatorResponedRequestActionType() {
        return RequestActionType.INSTALLATION_AUDIT_OPERATOR_RESPONDED;
    }
}
