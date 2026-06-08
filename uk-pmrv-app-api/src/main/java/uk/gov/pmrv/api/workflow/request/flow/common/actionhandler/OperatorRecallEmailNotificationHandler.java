package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInfoService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public abstract class OperatorRecallEmailNotificationHandler implements RequestRecallEmailNotificationHandler {

    private final UserAuthService userAuthService;
    private final AccountQueryService accountQueryService;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final VerifierAuthorityQueryService verifierAuthorityQueryService;
    private final VerifierUserInfoService verifierUserInfoService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    protected abstract String getType();

    @Override
    public void sendRecallEmailNotification(final Request request) {
        Long accountId = request.getAccountId();
        AccountType accountType = accountQueryService.getAccountType(accountId);

        if (accountType == AccountType.INSTALLATION) {

            InstallationAccountDTO account = installationAccountQueryService.getAccountDTOById(accountId);
            String verifierEmail = getVerifierEmail(account);
            if (verifierEmail == null) {
                return;
            }

            Map<String, Object> templateParams = getTemplateParams(account, request.getId(), getType(), accountType);

            final EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
                            .notificationTemplateData(
                                    PmrvEmailNotificationTemplateData.builder()
                                            .competentAuthority(request.getCompetentAuthority())
                                            .templateName(PmrvNotificationTemplateName.RECALLED_BY_OPERATOR.getName())
                                            .templateParams(templateParams)
                                            .accountType(accountType)
                                            .build())
                            .build();

            notificationEmailService.notifyRecipient(emailData, verifierEmail);
        }
    }

    private String getVerifierEmail(final   InstallationAccountDTO account) {
        Long verificationBodyId = account.getVerificationBodyId();
        if (verificationBodyId == null) {
            return null;
        }

        List<String> verifierAdmins = verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(verificationBodyId);
        if (verifierAdmins == null || verifierAdmins.isEmpty()) {
            return null;
        }

        List<UserInfoDTO> verifierUserInfoList = verifierUserInfoService.getVerifierUsersInfo(verifierAdmins);
        if (verifierUserInfoList == null || verifierUserInfoList.isEmpty()) {
            return null;
        }

        UserInfoDTO verifierUserInfo = verifierUserInfoList.getFirst();
        if (verifierUserInfo == null || verifierUserInfo.getEmail() == null) {
            return null;
        }
        return verifierUserInfo.getEmail();
    }

    protected Map<String, Object> getTemplateParams(InstallationAccountDTO account, String workflowId,
                                                    String type, AccountType accountType) {
        String operatorName = account.getLegalEntity() != null ? account.getLegalEntity().getName() : null;

        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(PmrvEmailNotificationTemplateConstants.RECALLED_BY_OPERATOR_TYPE, type);
        templateParams.put(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, operatorName);
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_ID, workflowId);
        templateParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_TYPE, accountType);
        templateParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, account.getName());

        return templateParams;
    }
}