package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType;
import uk.gov.pmrv.api.notification.message.service.SystemMessageNotificationService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountVerificationBodyNotificationServiceTest {

    @InjectMocks
    private AccountVerificationBodyNotificationService service;
    
    @Mock
    private SystemMessageNotificationService systemMessageNotificationService;
    
    @Mock
    private VerifierAuthorityQueryService verifierAuthorityQueryService;
    
    @Mock
    private OperatorAuthorityService operatorAuthorityService;
    
    @Test
    void notifyUsersForVerificationBodyApppointment() {
        Long verificationBodyId = 1L;
        Long accountId = 25L;
        String accountName = "accountName";
        String verifierAdmin = "veradmin";
        String emitterId = "emitterId";
        Account account = Mockito.mock(Account.class);
        when(account.getId()).thenReturn(accountId);
        when(account.getName()).thenReturn(accountName);
        when(account.getEmitterId()).thenReturn(emitterId);

        when(verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(verificationBodyId))
            .thenReturn(List.of(verifierAdmin));
        
        //invoke
        service.notifyUsersForVerificationBodyApppointment(verificationBodyId, account);
        
        verify(verifierAuthorityQueryService, times(1)).findVerifierAdminsByVerificationBody(verificationBodyId);
        ArgumentCaptor<SystemMessageNotificationInfo> messageCaptor = ArgumentCaptor.forClass(SystemMessageNotificationInfo.class);
        verify(systemMessageNotificationService, times(1)).generateAndSendNotificationSystemMessage(messageCaptor.capture());
        SystemMessageNotificationInfo message = messageCaptor.getValue();
        assertThat(message.getMessageType()).isEqualTo(SystemMessageNotificationType.NEW_VERIFICATION_BODY_EMITTER);
        assertThat(message.getAccountId()).isEqualTo(account.getId());
        assertThat(message.getReceiver()).isEqualTo(verifierAdmin);
        assertThat(message.getMessageParameters())
                        .containsExactlyInAnyOrderEntriesOf(Map.of(
                                "emitterName", account.getName(),
                                "emitterId", account.getEmitterId()));
    }
    
    @Test
    void notifyUsersForVerificationBodyUnapppointment() {
        Long accountId = 1L;
        String operatorAdmin = "opAdmin";
        List<String> operatorAdmins = List.of(operatorAdmin);
        Account account = Mockito.mock(Account.class);
        when(account.getId()).thenReturn(accountId);
        Set<Account> accountsUnappointed = Set.of(account);
        
        when(operatorAuthorityService.findActiveOperatorAdminUsersByAccount(accountId)).thenReturn(operatorAdmins);
        
        //invoke
        service.notifyUsersForVerificationBodyUnapppointment(accountsUnappointed);
        
        verify(operatorAuthorityService, times(1)).findActiveOperatorAdminUsersByAccount(accountId);
        
        ArgumentCaptor<SystemMessageNotificationInfo> messageCaptor = ArgumentCaptor.forClass(
            SystemMessageNotificationInfo.class);
        verify(systemMessageNotificationService, times(1)).generateAndSendNotificationSystemMessage(messageCaptor.capture());
        SystemMessageNotificationInfo message = messageCaptor.getValue();
        assertThat(message.getMessageType()).isEqualTo(SystemMessageNotificationType.VERIFIER_NO_LONGER_AVAILABLE);
        assertThat(message.getReceiver()).isEqualTo(operatorAdmin);
        assertThat(message.getMessageParameters())
        .containsExactlyInAnyOrderEntriesOf(Map.of(
                "accountId", account.getId()));
    }
}
