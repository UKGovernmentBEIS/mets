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
import java.util.Objects;
import java.util.Collections;

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

            Long verificationBodyId = account.getVerificationBodyId();
            if (verificationBodyId == null) {
                return;
            }

            List<String> verifierEmails = getAssignedVerifierEmails(request);
            if (verifierEmails.isEmpty()) {
                verifierEmails = getVerifierAdminEmails(verificationBodyId);
            }

            if (verifierEmails.isEmpty()) {
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

            verifierEmails.forEach(email ->
                    notificationEmailService.notifyRecipient(emailData, email));
        }
    }

    private List<String> getAssignedVerifierEmails(Request request) {
        String assignee = request.getPayload() != null ? request.getPayload().getVerifierAssignee() : null;

        if (assignee == null) {
            return Collections.emptyList();
        }

        UserInfoDTO user = userAuthService.getUserByUserId(assignee);
        if (user == null || user.getEmail() == null) {
            return Collections.emptyList();
        }

        return List.of(user.getEmail());
    }

    private List<String> getVerifierAdminEmails(Long verificationBodyId) {
        List<String> verifierAdmins = verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(verificationBodyId);

        if (verifierAdmins == null || verifierAdmins.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserInfoDTO> verifierAdminUsers = verifierUserInfoService.getVerifierUsersInfo(verifierAdmins);

        if (verifierAdminUsers == null || verifierAdminUsers.isEmpty()) {
            return Collections.emptyList();
        }

        return verifierAdminUsers.stream()
                .map(UserInfoDTO::getEmail)
                .filter(Objects::nonNull)
                .toList();
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