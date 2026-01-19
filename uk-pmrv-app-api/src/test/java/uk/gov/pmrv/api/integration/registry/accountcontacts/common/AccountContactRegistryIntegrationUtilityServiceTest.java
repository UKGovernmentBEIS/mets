package uk.gov.pmrv.api.integration.registry.accountcontacts.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsMessage;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserManagementService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountContactRegistryIntegrationUtilityServiceTest {

    private static final long ACCOUNT_ID = 1L;
    private static final int REGISTRY_ID = 1;

    private static final String ACTIVE_OP_ID = "activeUserId";
    private static final String INACTIVE_OP_ID = "inactiveUserId";
    private static final String VERIFIER_OP_ID = "verifierUserId";
    private static final String ACTIVE_LEAD_OP_ID = "leadOpUserId";

    @Mock
    private OperatorUserManagementService operatorUserManagementService;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;

    @InjectMocks
    private AccountContactRegistryIntegrationUtilityService accountContactRegistryIntegrationUtilityService;

    @Test
    void buildMetsContactsEvent_success() {
        Account account = buildAccount();
        List<AuthorityRoleDTO> authorityRoleDTOList = buildAuthorityRoleDTOList();
        OperatorUserDTO activeOperatorUserDTO = buildOperatorUserDTO(ACTIVE_OP_ID, "Active", "User", "op", "operator");
        OperatorUserDTO leadOperatorUserDTO = buildOperatorUserDTO(ACTIVE_LEAD_OP_ID, "Lead", "User", "lead", "operator_admin");

        when(operatorAuthorityQueryService.findOperatorUserAuthoritiesListByAccount(ACCOUNT_ID))
                .thenReturn(authorityRoleDTOList);
        when(operatorUserManagementService.getOperatorUserByAccountAndId(ACCOUNT_ID, ACTIVE_OP_ID))
                .thenReturn(activeOperatorUserDTO);
        when(operatorUserManagementService.getOperatorUserByAccountAndId(ACCOUNT_ID, ACTIVE_LEAD_OP_ID))
                .thenReturn(leadOperatorUserDTO);

        MetsContactsEvent result = accountContactRegistryIntegrationUtilityService.buildMetsContactsEvent(account);

        assertNotNull(result);
        assertEquals(String.valueOf(REGISTRY_ID), result.getOperatorId());
        assertEquals(2, result.getDetails().size());

        MetsContactsMessage activeOpMessage = result.getDetails().stream()
                .filter(m -> m.getEmail().equals("op.user@test.com"))
                .findFirst().orElse(null);
        assertNotNull(activeOpMessage);
        assertEquals("Active", activeOpMessage.getFirstName());
        assertEquals("OPERATOR_ADMIN", activeOpMessage.getUserType());
        assertEquals(1, activeOpMessage.getContactTypes().size());
        assertEquals(AccountContactType.FINANCIAL.name(), activeOpMessage.getContactTypes().get(0));

        MetsContactsMessage leadOpMessage = result.getDetails().stream()
                .filter(m -> m.getEmail().equals("lead.user@test.com"))
                .findFirst().orElse(null);
        assertNotNull(leadOpMessage);
        assertEquals("Lead", leadOpMessage.getFirstName());
        assertEquals("EMITTER", leadOpMessage.getUserType());
        assertEquals(1, leadOpMessage.getContactTypes().size());
        assertEquals(AccountContactType.SERVICE.name(), leadOpMessage.getContactTypes().get(0));

        verify(operatorAuthorityQueryService).findOperatorUserAuthoritiesListByAccount(ACCOUNT_ID);
        verify(operatorUserManagementService).getOperatorUserByAccountAndId(ACCOUNT_ID, ACTIVE_OP_ID);
        verify(operatorUserManagementService).getOperatorUserByAccountAndId(ACCOUNT_ID, ACTIVE_LEAD_OP_ID);
        verifyNoMoreInteractions(operatorUserManagementService);
    }

    @Test
    void buildMetsContactsEvent_no_active_users() {
        Account account = buildAccount();
        List<AuthorityRoleDTO> emptyList = List.of();

        when(operatorAuthorityQueryService.findOperatorUserAuthoritiesListByAccount(ACCOUNT_ID))
                .thenReturn(emptyList);

        MetsContactsEvent result = accountContactRegistryIntegrationUtilityService.buildMetsContactsEvent(account);

        assertNotNull(result);
        assertEquals(String.valueOf(REGISTRY_ID), result.getOperatorId());
        assertEquals(0, result.getDetails().size());

        verify(operatorAuthorityQueryService).findOperatorUserAuthoritiesListByAccount(ACCOUNT_ID);
        verifyNoMoreInteractions(operatorUserManagementService);
    }

    private Account buildAccount() {
        return InstallationAccount.builder()
                .id(ACCOUNT_ID)
                .registryId(REGISTRY_ID)
                .contacts(Map.of(
                        AccountContactType.FINANCIAL, ACTIVE_OP_ID,
                        AccountContactType.SERVICE, ACTIVE_LEAD_OP_ID
                ))
                .build();
    }

    private List<AuthorityRoleDTO> buildAuthorityRoleDTOList() {
        return List.of(
                AuthorityRoleDTO.builder()
                        .userId(ACTIVE_OP_ID)
                        .authorityStatus(AuthorityStatus.ACTIVE)
                        .roleCode("operator_admin")
                        .build(),
                AuthorityRoleDTO.builder()
                        .userId(INACTIVE_OP_ID)
                        .authorityStatus(AuthorityStatus.DISABLED)
                        .roleCode("operator_admin")
                        .build(),
                AuthorityRoleDTO.builder()
                        .userId(VERIFIER_OP_ID)
                        .authorityStatus(AuthorityStatus.ACTIVE)
                        .roleCode("verifier_admin")
                        .build(),
                AuthorityRoleDTO.builder()
                        .userId(ACTIVE_LEAD_OP_ID)
                        .authorityStatus(AuthorityStatus.ACTIVE)
                        .roleCode("emitter_contact")
                        .build()
        );
    }

    private OperatorUserDTO buildOperatorUserDTO(String userId, String firstName, String lastName, String emailPrefix, String roleCode) {
        return OperatorUserDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(emailPrefix + ".user@test.com")
                .phoneNumber(PhoneNumberDTO.builder().countryCode("30").number("2101234567").build())
                .mobileNumber(PhoneNumberDTO.builder().countryCode("30").number("6901234567").build())
                .build();
    }
}