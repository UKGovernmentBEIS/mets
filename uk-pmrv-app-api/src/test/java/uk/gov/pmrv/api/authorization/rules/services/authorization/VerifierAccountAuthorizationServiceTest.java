package uk.gov.pmrv.api.authorization.rules.services.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@ExtendWith(MockitoExtension.class)
class VerifierAccountAuthorizationServiceTest {

    @InjectMocks
    private VerifierAccountAuthorizationService verifierAccountAuthorizationService;

    @Mock
    private AccountAuthorityInfoProvider accountAuthorityInfoProvider;

    @Mock
    private VerifierVerificationBodyAuthorizationService verifierVerificationBodyAuthorizationService;

    @Mock
    private VerifierAccountAccessService verifierAccountAccessService;

    private final PmrvUser verifierUser = PmrvUser.builder().userId("userId").roleType(RoleType.VERIFIER).build();

    @Test
    void isAuthorized_account_true() {
        Long accountId = 1L;
        Long accountVerificationBody = 1L;

        when(accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId)).thenReturn(Optional.of(accountVerificationBody));
        when(verifierVerificationBodyAuthorizationService.isAuthorized(verifierUser, accountVerificationBody))
            .thenReturn(true);
        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser)).thenReturn(Set.of(1L, 2L));

        assertTrue(verifierAccountAuthorizationService.isAuthorized(verifierUser, accountId));
    }

    @Test
    void isAuthorized_account_false() {
        Long accountId = 1L;

        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser)).thenReturn(Set.of(2L));

        assertFalse(verifierAccountAuthorizationService.isAuthorized(verifierUser, accountId));
    }

    @Test
    void isAuthorized_account_no_verification_body() {
        Long accountId = 1L;

        when(accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId)).thenReturn(Optional.empty());
        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser)).thenReturn(Set.of(1L, 2L));

        assertFalse(verifierAccountAuthorizationService.isAuthorized(verifierUser, accountId));
        verifyNoInteractions(verifierVerificationBodyAuthorizationService);
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        Long accountId = 1L;
        Long accountVerificationBody = 1L;
        Permission permission = Permission.PERM_VB_USERS_EDIT;

        when(accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId)).thenReturn(Optional.of(accountVerificationBody));
        when(verifierVerificationBodyAuthorizationService.isAuthorized(verifierUser, accountVerificationBody, permission))
            .thenReturn(true);
        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser)).thenReturn(Set.of(1L, 2L));

        assertTrue(verifierAccountAuthorizationService.isAuthorized(verifierUser, accountId, permission));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        Long accountId = 1L;
        Long accountVerificationBody = 1L;
        Permission permission = Permission.PERM_VB_USERS_EDIT;

        when(accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId)).thenReturn(Optional.of(accountVerificationBody));
        when(verifierVerificationBodyAuthorizationService.isAuthorized(verifierUser, accountVerificationBody, permission))
            .thenReturn(false);
        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser)).thenReturn(Set.of(1L, 2L));

        assertFalse(verifierAccountAuthorizationService.isAuthorized(verifierUser, accountId, permission));
    }

    @Test
    void isAuthorized_account_no_verification_body_with_permissions() {
        Long accountId = 1L;
        Permission permission = Permission.PERM_VB_USERS_EDIT;

        when(accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId)).thenReturn(Optional.empty());
        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser)).thenReturn(Set.of(1L, 2L));

        assertFalse(verifierAccountAuthorizationService.isAuthorized(verifierUser, accountId, permission));
        verifyNoInteractions(verifierVerificationBodyAuthorizationService);
    }

    @Test
    void isAuthorized_account_when_not_admin_and_task_exists() {

        Long accountId = 1L;
        Long accountVerificationBody = 2L;
        Permission permission = Permission.PERM_VB_USERS_EDIT;

        when(accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId)).thenReturn(Optional.of(accountVerificationBody));
        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser)).thenReturn(Set.of(1L));
        when(verifierVerificationBodyAuthorizationService.isAuthorized(verifierUser, accountVerificationBody, permission))
            .thenReturn(true);

        assertTrue(verifierAccountAuthorizationService.isAuthorized(verifierUser, accountId, permission));
    }

    @Test
    void isAuthorized_account_when_not_admin_and_task_not_exists() {

        Long accountId = 1L;
        Permission permission = Permission.PERM_VB_USERS_EDIT;

        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser)).thenReturn(Set.of(2L));

        assertFalse(verifierAccountAuthorizationService.isAuthorized(verifierUser, accountId, permission));
        verifyNoInteractions(verifierVerificationBodyAuthorizationService);
    }

    @Test
    void getType() {
        assertEquals(RoleType.VERIFIER, verifierAccountAuthorizationService.getRoleType());
    }
}