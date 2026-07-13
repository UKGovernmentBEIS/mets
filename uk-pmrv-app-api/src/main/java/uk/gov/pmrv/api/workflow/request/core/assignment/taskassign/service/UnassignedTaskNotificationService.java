package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInfoService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UnassignedTaskNotificationService {

    private final WebAppProperties webAppProperties;
    private final AccountRepository accountRepository;
    private final VerifierAuthorityQueryService verifierAuthorityQueryService;
    private final VerifierUserInfoService verifierUserInfoService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    public void sendUnassignedTaskNotificationService(final RequestTask requestTask) {
        Request request = requestTask.getRequest();
        Long accountId = request.getAccountId();
        Long verificationBodyId = request.getVerificationBodyId();

        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty() || verificationBodyId == null) {
            return;
        }

        Map<String, Object> templateParams = getTemplateParams(account.get(), request.getId(), webAppProperties.getUrl(), requestTask.getType());

        final EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
                .notificationTemplateData(
                        PmrvEmailNotificationTemplateData.builder()
                                .competentAuthority(request.getCompetentAuthority())
                                .templateName(PmrvNotificationTemplateName.NEW_UNASSIGNED_TASK_RECEIVED.getName())
                                .templateParams(templateParams)
                                .accountType(account.get().getAccountType())
                                .build())
                .build();

        List<String> verifierAdmins = verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(verificationBodyId);
        if (verifierAdmins == null || verifierAdmins.isEmpty()) {
            return;
        }
        List<UserInfoDTO> verifierUsersInfo = verifierUserInfoService.getVerifierUsersInfo(verifierAdmins);
        if (verifierUsersInfo == null || verifierUsersInfo.isEmpty()) {
            return;
        }
        verifierUsersInfo.stream()
                .map(UserInfoDTO::getEmail)
                .filter(Objects::nonNull)
                .forEach(email ->
                        notificationEmailService.notifyRecipient(emailData, email));
    }

    private Map<String, Object> getTemplateParams(Account account, String workflowId, String homePage, RequestTaskType requestTaskType) {
        Map<String, Object> templateParams = new HashMap<>();

        templateParams.put(PmrvEmailNotificationTemplateConstants.REQUEST_TASK_TYPE, requestTaskType.name().replace("_", " "));
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_ID, workflowId);
        templateParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_TYPE, account.getAccountType());
        templateParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, account.getName());
        templateParams.put(PmrvEmailNotificationTemplateConstants.HOME_URL, homePage);

        String operatorName = account.getLegalEntity() != null ? account.getLegalEntity().getName() : null;
        templateParams.put(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, operatorName);

        return templateParams;
    }
}
