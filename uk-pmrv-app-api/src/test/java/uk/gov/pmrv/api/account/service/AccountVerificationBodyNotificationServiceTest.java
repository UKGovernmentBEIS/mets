package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.notificationapi.system.SystemNotificationInfo;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.notification.system.SystemNotificationProcessAndSendService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

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
    private SystemNotificationProcessAndSendService systemMessageNotificationService;
    
    @Mock
    private VerifierAuthorityQueryService verifierAuthorityQueryService;
    
    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;
    
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
        ArgumentCaptor<SystemNotificationInfo> messageCaptor = ArgumentCaptor.forClass(SystemNotificationInfo.class);
        verify(systemMessageNotificationService, times(1)).processAndSend(messageCaptor.capture());
        SystemNotificationInfo message = messageCaptor.getValue();
        assertThat(message.getTemplate()).isEqualTo(PmrvNotificationTemplateName.NEW_VERIFICATION_BODY_EMITTER.getName());
        assertThat(message.getAccountId()).isEqualTo(account.getId());
        assertThat(message.getReceiver()).isEqualTo(verifierAdmin);
        assertThat(message.getParameters())
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
        
        when(operatorAuthorityQueryService.findActiveOperatorAdminUsersByAccount(accountId)).thenReturn(operatorAdmins);
        
        //invoke
        service.notifyUsersForVerificationBodyUnapppointment(accountsUnappointed);
        
        verify(operatorAuthorityQueryService, times(1)).findActiveOperatorAdminUsersByAccount(accountId);
        
        ArgumentCaptor<SystemNotificationInfo> messageCaptor = ArgumentCaptor.forClass(
        		SystemNotificationInfo.class);
        verify(systemMessageNotificationService, times(1)).processAndSend(messageCaptor.capture());
        SystemNotificationInfo message = messageCaptor.getValue();
        assertThat(message.getTemplate()).isEqualTo(PmrvNotificationTemplateName.VERIFIER_NO_LONGER_AVAILABLE.getName());
        assertThat(message.getReceiver()).isEqualTo(operatorAdmin);
        assertThat(message.getParameters())
        .containsExactlyInAnyOrderEntriesOf(Map.of(
                "accountId", account.getId()));
    }
}
