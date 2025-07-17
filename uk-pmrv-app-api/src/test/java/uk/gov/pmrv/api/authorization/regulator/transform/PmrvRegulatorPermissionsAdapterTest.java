package uk.gov.pmrv.api.authorization.regulator.transform;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel;
import uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_ACCOUNT_USERS_EDIT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_CA_USERS_EDIT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_TASK_ASSIGNMENT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_VB_MANAGE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.ADD_OPERATOR_ADMIN;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.ASSIGN_REASSIGN_TASKS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_VERIFICATION_BODIES;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.VIEW_ONLY;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_NOTIFICATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_NOTIFICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_REVOCATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_REVOCATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_SURRENDER_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_SURRENDER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_APPLICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_NOTIFICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_REVOCATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_SURRENDER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_TRANSFER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_VARIATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_AER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_AIR;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_INSTALLATION_ACCOUNT;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_PERMIT_APPLICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_PERMIT_NOTIFICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_PERMIT_SURRENDER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_PERMIT_TRANSFER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_VIR;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_PERMIT_REVOCATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_REVIEW_PERMIT_VARIATION;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PmrvRegulatorPermissionsAdapterTest {
    private final PmrvRegulatorPermissionsAdapter pmrvRegulatorPermissionsAdapter = new PmrvRegulatorPermissionsAdapter();

    @BeforeAll
    void beforeAll() {
        pmrvRegulatorPermissionsAdapter.afterPropertiesSet();
    }

    @Test
    void getPermissionsFromPermissionGroupLevels_one_permission_per_group_level() {
        Map<String, RegulatorPermissionLevel> permissionGroupLevels =
                Map.of(REVIEW_INSTALLATION_ACCOUNT, VIEW_ONLY,
                        MANAGE_USERS_AND_CONTACTS, RegulatorPermissionLevel.NONE,
                        ADD_OPERATOR_ADMIN, RegulatorPermissionLevel.NONE,
                        ASSIGN_REASSIGN_TASKS, EXECUTE);

        List<String> expectedPermissions = List.of(
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
                PERM_TASK_ASSIGNMENT);

        assertThat(pmrvRegulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
                .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionsFromPermissionGroupLevels_multiple_permissions_per_group_level() {
        Map<String, RegulatorPermissionLevel> permissionGroupLevels =
                Map.of(REVIEW_INSTALLATION_ACCOUNT, EXECUTE,
                        MANAGE_USERS_AND_CONTACTS, RegulatorPermissionLevel.NONE,
                        ADD_OPERATOR_ADMIN, RegulatorPermissionLevel.NONE,
                        ASSIGN_REASSIGN_TASKS, EXECUTE);

        List<String> expectedPermissions = List.of(
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
                PERM_TASK_ASSIGNMENT);

        assertThat(pmrvRegulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
                .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionsFromPermissionGroupLevels_multiple_permissions() {
        Map<String, RegulatorPermissionLevel> permissionGroupLevels = Map.ofEntries(
                Map.entry(REVIEW_INSTALLATION_ACCOUNT, EXECUTE),
                Map.entry(MANAGE_USERS_AND_CONTACTS, EXECUTE),
                Map.entry(ADD_OPERATOR_ADMIN, EXECUTE),
                Map.entry(ASSIGN_REASSIGN_TASKS, EXECUTE),
                Map.entry(MANAGE_VERIFICATION_BODIES, EXECUTE),
                Map.entry(REVIEW_PERMIT_APPLICATION, EXECUTE),
                Map.entry(PEER_REVIEW_PERMIT_APPLICATION, EXECUTE),
                Map.entry(REVIEW_PERMIT_SURRENDER, EXECUTE),
                Map.entry(PEER_REVIEW_PERMIT_SURRENDER, EXECUTE),
                Map.entry(SUBMIT_PERMIT_REVOCATION, EXECUTE),
                Map.entry(PEER_REVIEW_PERMIT_REVOCATION, EXECUTE));

        List<String> expectedPermissions = List.of(
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
                PERM_CA_USERS_EDIT,
                PERM_ACCOUNT_USERS_EDIT,
                PERM_TASK_ASSIGNMENT,
                PERM_VB_MANAGE,
                PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK,
                PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK,
                PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK,
                PERM_PERMIT_SURRENDER_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK,
                PERM_PERMIT_SURRENDER_PEER_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK,
                PERM_PERMIT_REVOCATION_SUBMIT_EXECUTE_TASK,
                PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK,
                PERM_PERMIT_REVOCATION_PEER_REVIEW_EXECUTE_TASK);

        assertThat(pmrvRegulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
                .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions_one_permission_per_group_level() {
        List<String> permissions = List.of(
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
                PERM_TASK_ASSIGNMENT);

        Map<String, RegulatorPermissionLevel> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(REVIEW_INSTALLATION_ACCOUNT, VIEW_ONLY);
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(ADD_OPERATOR_ADMIN, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_VERIFICATION_BODIES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_SURRENDER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_SURRENDER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(SUBMIT_PERMIT_REVOCATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_REVOCATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_NOTIFICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_NOTIFICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(SUBMIT_REVIEW_PERMIT_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_TRANSFER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_TRANSFER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_VIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_AIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_EMP_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_NER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_NER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_DOAL, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_DOAL, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_AVIATION_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_AVIATION_VIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_AUDIT, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_ONSITE_INSPECTION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_AUDIT, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_ONSITE_INSPECTION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_AER_ANNUAL_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_ANNUAL_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_AER_3YEAR_PERIOD_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_3YEAR_PERIOD_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.MARK_NOT_REQUIRED_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_BDR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_BDR_REVIEW, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_PERMANENT_CESSATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMANENT_CESSATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DOE_CORSIA, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DOE_CORSIA, RegulatorPermissionLevel.NONE);

        assertThat(pmrvRegulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(permissions))
                .containsExactlyInAnyOrderEntriesOf(expectedPermissionGroupLevels);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions_multiple_permissions_per_group_level() {
        List<String> permissions = List.of(
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
                PERM_TASK_ASSIGNMENT);

        Map<String, RegulatorPermissionLevel> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(REVIEW_INSTALLATION_ACCOUNT, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(ADD_OPERATOR_ADMIN, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_VERIFICATION_BODIES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_SURRENDER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_SURRENDER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(SUBMIT_PERMIT_REVOCATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_REVOCATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_NOTIFICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_NOTIFICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(SUBMIT_REVIEW_PERMIT_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_TRANSFER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_TRANSFER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_VIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_AIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_EMP_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_NER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_NER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_DOAL, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_DOAL, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_AVIATION_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_AVIATION_VIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_AUDIT, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_ONSITE_INSPECTION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_AUDIT, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_ONSITE_INSPECTION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_AER_ANNUAL_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_ANNUAL_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_AER_3YEAR_PERIOD_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_3YEAR_PERIOD_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.MARK_NOT_REQUIRED_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_BDR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_BDR_REVIEW, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_PERMANENT_CESSATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMANENT_CESSATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DOE_CORSIA, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DOE_CORSIA, RegulatorPermissionLevel.NONE);

        assertThat(pmrvRegulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(permissions))
                .containsExactlyInAnyOrderEntriesOf(expectedPermissionGroupLevels);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions() {
        List<String> permissions = List.of(
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
                PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
                PERM_CA_USERS_EDIT,
                PERM_ACCOUNT_USERS_EDIT,
                PERM_TASK_ASSIGNMENT,
                PERM_VB_MANAGE,
                PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK,
                PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK,
                PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK,
                PERM_PERMIT_SURRENDER_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK,
                PERM_PERMIT_SURRENDER_PEER_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK,
                PERM_PERMIT_REVOCATION_SUBMIT_EXECUTE_TASK,
                PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK,
                PERM_PERMIT_REVOCATION_PEER_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK,
                PERM_PERMIT_NOTIFICATION_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK,
                PERM_PERMIT_NOTIFICATION_PEER_REVIEW_EXECUTE_TASK);

        Map<String, RegulatorPermissionLevel> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(REVIEW_INSTALLATION_ACCOUNT, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, EXECUTE);
        expectedPermissionGroupLevels.put(ADD_OPERATOR_ADMIN, EXECUTE);
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_VERIFICATION_BODIES, EXECUTE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_APPLICATION, EXECUTE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_APPLICATION, EXECUTE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_SURRENDER, EXECUTE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_SURRENDER, EXECUTE);
        expectedPermissionGroupLevels.put(SUBMIT_PERMIT_REVOCATION, EXECUTE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_REVOCATION, EXECUTE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_NOTIFICATION, EXECUTE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_NOTIFICATION, EXECUTE);
        expectedPermissionGroupLevels.put(SUBMIT_REVIEW_PERMIT_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_TRANSFER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_TRANSFER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_VIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(REVIEW_AIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_EMP_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_NER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_NER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_DOAL, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_DOAL, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_AVIATION_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_AVIATION_VIR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_AUDIT, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_ONSITE_INSPECTION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_AUDIT, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_ONSITE_INSPECTION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_AER_ANNUAL_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_ANNUAL_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_AER_3YEAR_PERIOD_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_3YEAR_PERIOD_OFFSETTING, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.MARK_NOT_REQUIRED_AER, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_BDR, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_BDR_REVIEW, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_PERMANENT_CESSATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMANENT_CESSATION, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DOE_CORSIA, RegulatorPermissionLevel.NONE);
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DOE_CORSIA, RegulatorPermissionLevel.NONE);

        assertThat(pmrvRegulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(permissions))
                .containsExactlyInAnyOrderEntriesOf(expectedPermissionGroupLevels);
    }

    @Test
    void getPermissionGroupLevels() {
        Map<String, List<RegulatorPermissionLevel>> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(REVIEW_INSTALLATION_ACCOUNT, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, List.of(RegulatorPermissionLevel.NONE, EXECUTE));
        expectedPermissionGroupLevels.put(ADD_OPERATOR_ADMIN, List.of(RegulatorPermissionLevel.NONE, EXECUTE));
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, List.of(RegulatorPermissionLevel.NONE, EXECUTE));
        expectedPermissionGroupLevels.put(MANAGE_VERIFICATION_BODIES, List.of(RegulatorPermissionLevel.NONE, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_APPLICATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_APPLICATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_SURRENDER, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_SURRENDER, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(SUBMIT_PERMIT_REVOCATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_REVOCATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_NOTIFICATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_NOTIFICATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(SUBMIT_REVIEW_PERMIT_VARIATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_VARIATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_TRANSFER, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_TRANSFER, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_AER, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_VIR, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_AIR, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_DRE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_DRE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_EMP_APPLICATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_NER, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_NER, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DRE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_DOAL, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_DOAL, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_AUDIT, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_ONSITE_INSPECTION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_AUDIT, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_ONSITE_INSPECTION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_AVIATION_AER,  List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.REVIEW_AVIATION_VIR,  List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_AER_ANNUAL_OFFSETTING, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_ANNUAL_OFFSETTING, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.AVIATION_AER_3YEAR_PERIOD_OFFSETTING, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_3YEAR_PERIOD_OFFSETTING, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.MARK_NOT_REQUIRED_AER, List.of(RegulatorPermissionLevel.NONE, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_BDR, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_BDR_REVIEW, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_PERMANENT_CESSATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMANENT_CESSATION, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DOE_CORSIA, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DOE_CORSIA, List.of(RegulatorPermissionLevel.NONE, VIEW_ONLY, EXECUTE));

        Map<String, List<RegulatorPermissionLevel>> actualPermissionGroupLevels =
                pmrvRegulatorPermissionsAdapter.getPermissionGroupLevels();

        assertThat(actualPermissionGroupLevels.keySet())
                .containsExactlyInAnyOrderElementsOf(expectedPermissionGroupLevels.keySet());
        actualPermissionGroupLevels.forEach((group, levels) ->
                assertThat(levels).containsExactlyElementsOf(expectedPermissionGroupLevels.get(group)));
    }
}