package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.notificationapi.system.SystemNotificationInfo;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.notification.system.SystemNotificationProcessAndSendService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AccountVerificationBodyNotificationService {
    
    private final SystemNotificationProcessAndSendService systemMessageNotificationService;
    private final VerifierAuthorityQueryService verifierAuthorityQueryService;
    private final OperatorAuthorityQueryService operatorAuthorityQueryService;

    public void notifyUsersForVerificationBodyApppointment(Long verificationBodyId, Account account) {
        List<String> verifierAdmins = verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(verificationBodyId);
        verifierAdmins
            .forEach(ver -> systemMessageNotificationService.processAndSend(
                    createNewVerificationBodyInstallationSystemMessage(account, ver)));
    }
    
    public void notifyUsersForVerificationBodyUnapppointment(Set<Account> accountsUnappointed) {
        accountsUnappointed
            .forEach(acc -> {
                List<String> operatorAdmins = operatorAuthorityQueryService.findActiveOperatorAdminUsersByAccount(acc.getId());
                operatorAdmins.forEach(op ->
                        systemMessageNotificationService.processAndSend(
                            createVerifierNoLongerAvailableSystemMessage(acc, op)));
            });
    }
    
    private SystemNotificationInfo createNewVerificationBodyInstallationSystemMessage(Account account, String verifierAdmin) {
        return SystemNotificationInfo.builder()
        		.template(PmrvNotificationTemplateName.NEW_VERIFICATION_BODY_EMITTER.getName())
                .parameters(Map.of(
                        "emitterName", account.getName(),
                        "emitterId", account.getEmitterId()))
                .accountId(account.getId())
                .receiver(verifierAdmin)
                .build();
    }
    
    private SystemNotificationInfo createVerifierNoLongerAvailableSystemMessage(
            Account account, String operatorAdmin) {
        return SystemNotificationInfo.builder()
        		.template(PmrvNotificationTemplateName.VERIFIER_NO_LONGER_AVAILABLE.getName())
                .parameters(Map.of("accountId", account.getId()))
                .accountId(account.getId())
                .receiver(operatorAdmin)
                .build();
    }
}
