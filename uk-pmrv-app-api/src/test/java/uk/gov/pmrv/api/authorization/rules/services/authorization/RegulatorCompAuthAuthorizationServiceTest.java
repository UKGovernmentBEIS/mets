package uk.gov.pmrv.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegulatorCompAuthAuthorizationServiceTest {
    private final RegulatorCompAuthAuthorizationService regulatorCompAuthAuthorizationService = new RegulatorCompAuthAuthorizationService();

    private final PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .permissions(List.of(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
                    Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK))
            .build();
    private final PmrvUser user = PmrvUser.builder().authorities(List.of(pmrvAuthority)).roleType(RoleType.REGULATOR).build();

    @Test
    void isAuthorized_account_true() {
        assertTrue(regulatorCompAuthAuthorizationService.isAuthorized(user, CompetentAuthorityEnum.ENGLAND));
    }

    @Test
    void isAuthorized_account_false() {
        assertFalse(regulatorCompAuthAuthorizationService.isAuthorized(user, CompetentAuthorityEnum.SCOTLAND));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        assertTrue(regulatorCompAuthAuthorizationService.isAuthorized(user, CompetentAuthorityEnum.ENGLAND,
                Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        assertFalse(regulatorCompAuthAuthorizationService.isAuthorized(user, CompetentAuthorityEnum.ENGLAND,
                Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK));
    }

    @Test
    void getType() {
        assertEquals(RoleType.REGULATOR, regulatorCompAuthAuthorizationService.getRoleType());
    }
}