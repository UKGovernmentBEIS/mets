package uk.gov.pmrv.api.common.domain.converter;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel;
import uk.gov.pmrv.api.authorization.regulator.transform.RegulatorPermissionsAdapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_ACCOUNT_USERS_EDIT;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_CA_USERS_EDIT;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_NOTIFICATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_NOTIFICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_REVOCATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_REVOCATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_SURRENDER_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_SURRENDER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_TASK_ASSIGNMENT;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_VB_MANAGE;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.ADD_OPERATOR_ADMIN;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.ASSIGN_REASSIGN_TASKS;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_VERIFICATION_BODIES;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.PEER_REVIEW_PERMIT_APPLICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.PEER_REVIEW_PERMIT_NOTIFICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.PEER_REVIEW_PERMIT_REVOCATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.PEER_REVIEW_PERMIT_SURRENDER;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.PEER_REVIEW_PERMIT_TRANSFER;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.PEER_REVIEW_PERMIT_VARIATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.REVIEW_AER;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.REVIEW_AIR;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.REVIEW_INSTALLATION_ACCOUNT;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.REVIEW_PERMIT_APPLICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.REVIEW_PERMIT_NOTIFICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.REVIEW_PERMIT_SURRENDER;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.REVIEW_PERMIT_TRANSFER;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.REVIEW_VIR;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.SUBMIT_PERMIT_REVOCATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.SUBMIT_REVIEW_PERMIT_VARIATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.VIEW_ONLY;

class RegulatorPermissionsAdapterTest {

    @Test
    void getPermissionsFromPermissionGroupLevels_one_permission_per_group_level() {
        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> permissionGroupLevels =
            Map.of(REVIEW_INSTALLATION_ACCOUNT, VIEW_ONLY,
                MANAGE_USERS_AND_CONTACTS, NONE,
                ADD_OPERATOR_ADMIN, NONE,
                ASSIGN_REASSIGN_TASKS, EXECUTE);

        List<Permission> expectedPermissions = List.of(
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
            PERM_TASK_ASSIGNMENT);

        assertThat(RegulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
            .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionsFromPermissionGroupLevels_multiple_permissions_per_group_level() {
        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> permissionGroupLevels =
            Map.of(REVIEW_INSTALLATION_ACCOUNT, EXECUTE,
                MANAGE_USERS_AND_CONTACTS, NONE,
                ADD_OPERATOR_ADMIN, NONE,
                ASSIGN_REASSIGN_TASKS, EXECUTE);

        List<Permission> expectedPermissions = List.of(
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
            PERM_TASK_ASSIGNMENT);

        assertThat(RegulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
            .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionsFromPermissionGroupLevels_multiple_permissions() {
        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> permissionGroupLevels = Map.ofEntries(
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

        List<Permission> expectedPermissions = List.of(
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

        assertThat(RegulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
            .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions_one_permission_per_group_level() {
        List<Permission> permissions = List.of(
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
            PERM_TASK_ASSIGNMENT);

        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(REVIEW_INSTALLATION_ACCOUNT, VIEW_ONLY);
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, NONE);
        expectedPermissionGroupLevels.put(ADD_OPERATOR_ADMIN, NONE);
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_VERIFICATION_BODIES, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_SURRENDER, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_SURRENDER, NONE);
        expectedPermissionGroupLevels.put(SUBMIT_PERMIT_REVOCATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_REVOCATION, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_NOTIFICATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_NOTIFICATION, NONE);
        expectedPermissionGroupLevels.put(SUBMIT_REVIEW_PERMIT_VARIATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_VARIATION, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_TRANSFER, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_TRANSFER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE, NONE);
        expectedPermissionGroupLevels.put(REVIEW_AER, NONE);
        expectedPermissionGroupLevels.put(REVIEW_VIR, NONE);
        expectedPermissionGroupLevels.put(REVIEW_AIR, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_EMP_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_NER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_NER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_AVIATION_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_DOAL, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_DOAL, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_AVIATION_AER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_AVIATION_VIR, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE, NONE);
        

        assertThat(RegulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(permissions))
            .containsExactlyInAnyOrderEntriesOf(expectedPermissionGroupLevels);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions_multiple_permissions_per_group_level() {
        List<Permission> permissions = List.of(
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
            PERM_TASK_ASSIGNMENT);

        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(REVIEW_INSTALLATION_ACCOUNT, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, NONE);
        expectedPermissionGroupLevels.put(ADD_OPERATOR_ADMIN, NONE);
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_VERIFICATION_BODIES, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_SURRENDER, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_SURRENDER, NONE);
        expectedPermissionGroupLevels.put(SUBMIT_PERMIT_REVOCATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_REVOCATION, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_NOTIFICATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_NOTIFICATION, NONE);
        expectedPermissionGroupLevels.put(SUBMIT_REVIEW_PERMIT_VARIATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_VARIATION, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_TRANSFER, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_TRANSFER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE, NONE);
        expectedPermissionGroupLevels.put(REVIEW_AER, NONE);
        expectedPermissionGroupLevels.put(REVIEW_VIR, NONE);
        expectedPermissionGroupLevels.put(REVIEW_AIR, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_EMP_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_NER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_NER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_AVIATION_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_DOAL, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_DOAL, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_AVIATION_AER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_AVIATION_VIR, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE, NONE);

        assertThat(RegulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(permissions))
            .containsExactlyInAnyOrderEntriesOf(expectedPermissionGroupLevels);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions() {
        List<Permission> permissions = List.of(
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
                Permission.PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK,
                PERM_PERMIT_NOTIFICATION_REVIEW_EXECUTE_TASK,
                PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK,
                PERM_PERMIT_NOTIFICATION_PEER_REVIEW_EXECUTE_TASK);

        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> expectedPermissionGroupLevels = new LinkedHashMap<>();
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
        expectedPermissionGroupLevels.put(SUBMIT_REVIEW_PERMIT_VARIATION, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_VARIATION, NONE);
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_TRANSFER, NONE);
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_TRANSFER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE, NONE);
        expectedPermissionGroupLevels.put(REVIEW_AER, NONE);
        expectedPermissionGroupLevels.put(REVIEW_VIR, NONE);
        expectedPermissionGroupLevels.put(REVIEW_AIR, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_EMP_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_NER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_NER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_AVIATION_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_DOAL, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_DOAL, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_AVIATION_AER, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_AVIATION_VIR, NONE);
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE, NONE);

        assertThat(RegulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(permissions))
            .containsExactlyInAnyOrderEntriesOf(expectedPermissionGroupLevels);
    }

    @Test
    void getPermissionGroupLevels() {
        Map<RegulatorPermissionGroup, List<RegulatorPermissionLevel>> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(REVIEW_INSTALLATION_ACCOUNT, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(ADD_OPERATOR_ADMIN, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(MANAGE_VERIFICATION_BODIES, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_APPLICATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_APPLICATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_SURRENDER, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_SURRENDER, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(SUBMIT_PERMIT_REVOCATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_REVOCATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_NOTIFICATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_NOTIFICATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(SUBMIT_REVIEW_PERMIT_VARIATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_VARIATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_PERMIT_TRANSFER, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(PEER_REVIEW_PERMIT_TRANSFER, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_AER, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_VIR, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(REVIEW_AIR, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_DRE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_DRE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_EMP_APPLICATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_NER, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_NER, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_AVIATION_DRE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_DOAL, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_DOAL, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION, List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_AVIATION_AER,  List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.REVIEW_AVIATION_VIR,  List.of(NONE, VIEW_ONLY, EXECUTE));
        expectedPermissionGroupLevels.put(RegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE, List.of(NONE, VIEW_ONLY, EXECUTE));

        Map<RegulatorPermissionGroup, List<RegulatorPermissionLevel>> actualPermissionGroupLevels =
            RegulatorPermissionsAdapter.getPermissionGroupLevels();

        assertThat(actualPermissionGroupLevels.keySet())
            .containsExactlyInAnyOrderElementsOf(expectedPermissionGroupLevels.keySet());
        actualPermissionGroupLevels.forEach((group, levels) ->
            assertThat(levels).containsExactlyElementsOf(expectedPermissionGroupLevels.get(group)));
    }
}
