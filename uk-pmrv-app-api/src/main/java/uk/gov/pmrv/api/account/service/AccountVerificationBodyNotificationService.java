package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType;
import uk.gov.pmrv.api.notification.message.service.SystemMessageNotificationService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AccountVerificationBodyNotificationService {
    
    private final SystemMessageNotificationService systemMessageNotificationService;
    private final VerifierAuthorityQueryService verifierAuthorityQueryService;
    private final OperatorAuthorityQueryService operatorAuthorityQueryService;

    public void notifyUsersForVerificationBodyApppointment(Long verificationBodyId, Account account) {
        List<String> verifierAdmins = verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(verificationBodyId);
        verifierAdmins
            .forEach(ver -> systemMessageNotificationService.generateAndSendNotificationSystemMessage(
                    createNewVerificationBodyInstallationSystemMessage(account, ver)));
    }
    
    public void notifyUsersForVerificationBodyUnapppointment(Set<Account> accountsUnappointed) {
        accountsUnappointed
            .forEach(acc -> {
                List<String> operatorAdmins = operatorAuthorityQueryService.findActiveOperatorAdminUsersByAccount(acc.getId());
                operatorAdmins.forEach(op ->
                        systemMessageNotificationService.generateAndSendNotificationSystemMessage(
                            createVerifierNoLongerAvailableSystemMessage(acc, op)));
            });
    }
    
    private SystemMessageNotificationInfo createNewVerificationBodyInstallationSystemMessage(Account account, String verifierAdmin) {
        return SystemMessageNotificationInfo.builder()
                .messageType(SystemMessageNotificationType.NEW_VERIFICATION_BODY_EMITTER)
                .messageParameters(Map.of(
                        "emitterName", account.getName(),
                        "emitterId", account.getEmitterId()))
                .accountId(account.getId())
                .receiver(verifierAdmin)
                .build();
    }
    
    private SystemMessageNotificationInfo createVerifierNoLongerAvailableSystemMessage(
            Account account, String operatorAdmin) {
        return SystemMessageNotificationInfo.builder()
                .messageType(SystemMessageNotificationType.VERIFIER_NO_LONGER_AVAILABLE)
                .messageParameters(Map.of("accountId", account.getId()))
                .accountId(account.getId())
                .receiver(operatorAdmin)
                .build();
    }
}
