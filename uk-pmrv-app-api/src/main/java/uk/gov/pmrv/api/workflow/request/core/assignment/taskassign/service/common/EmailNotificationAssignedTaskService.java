package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.common;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

@Log4j2
@Service
@AllArgsConstructor
public class EmailNotificationAssignedTaskService {

    private final NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
    private final UserAuthService userAuthService;
    private final WebAppProperties webAppProperties;
    private final AccountQueryService accountQueryService;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final AviationAccountQueryService aviationAccountQueryService;

    /**
     * Sends an email notification to the specified recipient.
     * This method retrieves the user's information based on the provided {@code userId}, constructs the email template
     * data, and sends the email to the recipient using the {@link NotificationEmailService}.
     *
     * @param userId the unique identifier of the recipient user. {@link String}.
     */
    public void sendEmailToRecipient(String userId, RequestTask requestTask, String role) {
        if (userId == null) {
            log.error("The userId cannot be null.");
            return;
        }
        UserInfoDTO userInfoDTO = userAuthService.getUserByUserId(userId);

        notificationEmailService.notifyRecipient(
            EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(constructEmailTemplateData(webAppProperties.getUrl(), requestTask, role))
                .attachments(Collections.emptyMap())
                .build(),
            userInfoDTO.getEmail()
        );
    }

    private EmailNotificationTemplateData constructEmailTemplateData(String homePage, RequestTask requestTask, String role) {
        EmailNotificationTemplateData emailNotificationTemplateData = defaultEmailNotificationTemplateData(homePage, requestTask, role);

        if (!isSupportedRole(role)) {
            return emailNotificationTemplateData;
        }

        Request request = requestTask.getRequest();
        Long accountId = request.getAccountId();
        AccountType accountType = accountQueryService.getAccountType(accountId);
        RequestTaskType taskType = requestTask.getType();

        if (RoleTaskPermissions.isAllowed(role, accountType, taskType)) {
            if (accountType == AccountType.INSTALLATION) {
                return buildInstallationTemplateData(emailNotificationTemplateData, request, accountId, accountType);
            }
            if (accountType == AccountType.AVIATION) {
                return buildAviationTemplateData(emailNotificationTemplateData, request, accountId, accountType);
            }
        }
        emailNotificationTemplateData.getTemplateParams().put(PmrvEmailNotificationTemplateConstants.HAS_WORKFLOW_ID, false);

        return emailNotificationTemplateData;
    }

    private boolean isSupportedRole(String role) {
        return Set.of(RoleTypeConstants.REGULATOR, RoleTypeConstants.VERIFIER, RoleTypeConstants.OPERATOR).contains(role);
    }

    private EmailNotificationTemplateData defaultEmailNotificationTemplateData (String homePage, RequestTask requestTask, String role) {
        Map<String, Object> templateParams = new HashMap<>(Map.ofEntries(
                entry(PmrvEmailNotificationTemplateConstants.REQUEST_TASK_TYPE, processRequestTaskTypeName(requestTask.getType())),
                entry(PmrvEmailNotificationTemplateConstants.HOME_URL, homePage),
                entry(PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, role)
        ));
        return EmailNotificationTemplateData.builder()
                .templateName(PmrvNotificationTemplateName. EMAIL_ASSIGNED_TASK.getName())
                .templateParams(templateParams)
                .build();
    }

    private EmailNotificationTemplateData buildInstallationTemplateData(EmailNotificationTemplateData emailNotificationTemplateData,
                                                                        Request request, Long accountId, AccountType accountType) {
        InstallationAccountDTO account = installationAccountQueryService.getAccountDTOById(accountId);
        if (account == null) {
            return emailNotificationTemplateData;
        }
        populateCommonParams(emailNotificationTemplateData, request.getId(),
                account.getLegalEntity() != null ? account.getLegalEntity().getName() : null,
                accountType);
        emailNotificationTemplateData.getTemplateParams().put(PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME,  account.getName());

        return emailNotificationTemplateData;
    }

    private EmailNotificationTemplateData buildAviationTemplateData(EmailNotificationTemplateData emailNotificationTemplateData,
                                                                    Request request, Long accountId, AccountType accountType) {
        AviationAccountDTO account = aviationAccountQueryService.getAviationAccountDTOById(accountId);
        if (account == null) {
            return emailNotificationTemplateData;
        }
        populateCommonParams(emailNotificationTemplateData, request.getId(), account.getName(), accountType);

        return emailNotificationTemplateData;
    }

    private void populateCommonParams(EmailNotificationTemplateData emailNotificationTemplateData, String workflowId,
                                      String operatorName,  AccountType accountType) {
        Map<String, Object> params = emailNotificationTemplateData.getTemplateParams();
        params.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_ID, workflowId);
        params.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_TYPE, accountType);
        params.put(PmrvEmailNotificationTemplateConstants.HAS_WORKFLOW_ID, true);
        if (operatorName != null) {
            params.put(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, operatorName);
        }
    }

    private String processRequestTaskTypeName(RequestTaskType requestTaskType) {

        String requestTaskTypeName = requestTaskType.name();
        requestTaskTypeName = requestTaskTypeName .replaceAll("_"," ").toUpperCase();

        return requestTaskTypeName;

    }
}
