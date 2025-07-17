package uk.gov.pmrv.api.authorization.regulator.transform;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroupLevel;
import uk.gov.netz.api.authorization.regulator.transform.AbstarctRegulatorPermissionsAdapter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_ACCOUNT_USERS_EDIT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_CA_USERS_EDIT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_TASK_ASSIGNMENT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_VB_MANAGE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.ADD_OPERATOR_ADMIN;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.ASSIGN_REASSIGN_TASKS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_VERIFICATION_BODIES;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.VIEW_ONLY;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AER_APPLICATION_MARK_NOT_REQUIRED_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AER_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AER_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AIR_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AIR_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_ACCOUNT_CLOSURE_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_ACCOUNT_CLOSURE_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_DOE_CORSIA_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_DOE_CORSIA_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_DRE_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_DRE_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_DRE_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_DRE_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_NON_COMPLIANCE_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_NON_COMPLIANCE_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_NON_COMPLIANCE_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_NON_COMPLIANCE_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_VIR_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_AVIATION_VIR_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_DOAL_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_DOAL_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_DOAL_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_DOAL_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_DRE_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_DRE_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_DRE_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_DRE_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_BATCH_REISSUE_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_BATCH_REISSUE_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_VARIATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_VARIATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_VARIATION_SUBMIT_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_EMP_VARIATION_SUBMIT_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_AUDIT_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_AUDIT_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_AUDIT_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_AUDIT_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_ONSITE_INSPECTION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_INSTALLATION_ONSITE_INSPECTION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_NER_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_NER_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_NER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_NER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_NON_COMPLIANCE_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_NON_COMPLIANCE_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_NON_COMPLIANCE_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_NON_COMPLIANCE_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMANENT_CESSATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMANENT_CESSATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_BATCH_REISSUE_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_BATCH_REISSUE_SUBMIT_VIEW_TASK;
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
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_TRANSFER_B_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_TRANSFER_B_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_TRANSFER_B_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_TRANSFER_B_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_VARIATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_VARIATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_VARIATION_SUBMIT_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMIT_VARIATION_SUBMIT_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_RETURN_OF_ALLOWANCES_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_RETURN_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_RETURN_OF_ALLOWANCES_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_RETURN_OF_ALLOWANCES_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_VIR_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_VIR_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_WITHHOLDING_OF_ALLOWANCES_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_WITHHOLDING_OF_ALLOWANCES_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_BDR_APPLICATION_PEER_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_BDR_APPLICATION_PEER_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMANENT_CESSATION_APPLICATION_SUBMIT_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.PmrvPermission.PERM_PERMANENT_CESSATION_APPLICATION_SUBMIT_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.AVIATION_ACCOUNT_CLOSURE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.AVIATION_AER_3YEAR_PERIOD_OFFSETTING;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.AVIATION_AER_ANNUAL_OFFSETTING;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.MARK_NOT_REQUIRED_AER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_3YEAR_PERIOD_OFFSETTING;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_AER_ANNUAL_OFFSETTING;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DOE_CORSIA;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_DRE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_AVIATION_NON_COMPLIANCE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_DOAL;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_DRE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_APPLICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_EMP_VARIATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_AUDIT;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_INSTALLATION_ONSITE_INSPECTION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_NER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_NON_COMPLIANCE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_APPLICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_NOTIFICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_REVOCATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_SURRENDER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_TRANSFER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMIT_VARIATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_RETURN_OF_ALLOWANCES;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_AER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_AIR;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_AVIATION_AER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_AVIATION_VIR;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_EMP_APPLICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_INSTALLATION_ACCOUNT;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_NER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_PERMIT_APPLICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_PERMIT_NOTIFICATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_PERMIT_SURRENDER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_PERMIT_TRANSFER;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_VIR;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DOE_CORSIA;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_DRE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_AVIATION_NON_COMPLIANCE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_DOAL;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_DRE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_EMP_BATCH_REISSUE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_AUDIT;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_INSTALLATION_ONSITE_INSPECTION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_NON_COMPLIANCE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_PERMIT_BATCH_REISSUE;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_PERMIT_REVOCATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_RETURN_OF_ALLOWANCES;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_REVIEW_EMP_VARIATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_REVIEW_PERMIT_VARIATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_WITHHOLDING_OF_ALLOWANCES;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_BDR;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_PERMANENT_CESSATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.PEER_REVIEW_PERMANENT_CESSATION;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.SUBMIT_BDR_REVIEW;

@Component
public class PmrvRegulatorPermissionsAdapter extends AbstarctRegulatorPermissionsAdapter implements InitializingBean {

    private final Map<RegulatorPermissionGroupLevel, List<String>> permissionGroupLevelsConfig = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() {

        //REVIEW_INSTALLATION_ACCOUNT
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_INSTALLATION_ACCOUNT, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_INSTALLATION_ACCOUNT, VIEW_ONLY),
                List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_INSTALLATION_ACCOUNT, EXECUTE),
                List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
                    PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK));

        //MANAGE_USERS_AND_CONTACTS
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(MANAGE_USERS_AND_CONTACTS, NONE),
                Collections.emptyList());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(MANAGE_USERS_AND_CONTACTS, EXECUTE),
                List.of(PERM_CA_USERS_EDIT));

        //ADD_OPERATOR_ADMIN
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ADD_OPERATOR_ADMIN, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ADD_OPERATOR_ADMIN, EXECUTE),
                List.of(PERM_ACCOUNT_USERS_EDIT));

        //ASSIGN_REASSIGN TASKS
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ASSIGN_REASSIGN_TASKS, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ASSIGN_REASSIGN_TASKS, EXECUTE),
                List.of(PERM_TASK_ASSIGNMENT));

        //MANAGE_VERIFICATION_BODIES
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_VERIFICATION_BODIES, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_VERIFICATION_BODIES, EXECUTE),
                        List.of(PERM_VB_MANAGE));

        // REVIEW_PERMIT_APPLICATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_APPLICATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_APPLICATION, VIEW_ONLY),
                List.of(PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_APPLICATION, EXECUTE),
                List.of(PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK,
                        PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK));

        // PEER_REVIEW_PERMIT_APPLICATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_APPLICATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_APPLICATION, VIEW_ONLY),
                List.of(PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_APPLICATION, EXECUTE),
                List.of(PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK,
                        PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK));

        // REVIEW_PERMIT_SURRENDER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_SURRENDER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_SURRENDER, VIEW_ONLY),
                List.of(PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_SURRENDER, EXECUTE),
                List.of(PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK,
                        PERM_PERMIT_SURRENDER_REVIEW_EXECUTE_TASK));

        // PEER_REVIEW_PERMIT_SURRENDER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_SURRENDER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_SURRENDER, VIEW_ONLY),
                List.of(PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_SURRENDER, EXECUTE),
                List.of(PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK,
                        PERM_PERMIT_SURRENDER_PEER_REVIEW_EXECUTE_TASK));

        // SUBMIT_PERMIT_REVOCATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_REVOCATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_REVOCATION, VIEW_ONLY),
                List.of(PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_REVOCATION, EXECUTE),
                List.of(PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK,
                    PERM_PERMIT_REVOCATION_SUBMIT_EXECUTE_TASK));

        // PEER_REVIEW_PERMIT_REVOCATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_REVOCATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_REVOCATION, VIEW_ONLY),
                List.of(PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_REVOCATION, EXECUTE),
                List.of(PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK,
                    PERM_PERMIT_REVOCATION_PEER_REVIEW_EXECUTE_TASK));

        // REVIEW_PERMIT_NOTIFICATION
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_NOTIFICATION, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_NOTIFICATION, VIEW_ONLY),
                        List.of(PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_NOTIFICATION, EXECUTE),
                        List.of(PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK,
                                PERM_PERMIT_NOTIFICATION_REVIEW_EXECUTE_TASK));

        // PEER_REVIEW_PERMIT_NOTIFICATION
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_NOTIFICATION, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_NOTIFICATION, VIEW_ONLY),
                        List.of(PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_NOTIFICATION, EXECUTE),
                        List.of(PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK,
                                PERM_PERMIT_NOTIFICATION_PEER_REVIEW_EXECUTE_TASK));

        // SUBMIT_REVIEW_PERMIT_VARIATION
        permissionGroupLevelsConfig
        		.put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_PERMIT_VARIATION, NONE), List.of());
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_PERMIT_VARIATION, VIEW_ONLY),
		                List.of(PERM_PERMIT_VARIATION_SUBMIT_REVIEW_VIEW_TASK));
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_PERMIT_VARIATION, EXECUTE),
		                List.of(PERM_PERMIT_VARIATION_SUBMIT_REVIEW_VIEW_TASK,
		                		PERM_PERMIT_VARIATION_SUBMIT_REVIEW_EXECUTE_TASK));

	    // PEER_REVIEW_PERMIT_VARIATION
        permissionGroupLevelsConfig
        		.put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_VARIATION, NONE), List.of());
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_VARIATION, VIEW_ONLY),
		                List.of(PERM_PERMIT_VARIATION_PEER_REVIEW_VIEW_TASK));
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_VARIATION, EXECUTE),
		                List.of(PERM_PERMIT_VARIATION_PEER_REVIEW_VIEW_TASK,
		                		PERM_PERMIT_VARIATION_PEER_REVIEW_EXECUTE_TASK));

        // REVIEW_PERMIT_TRANSFER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_TRANSFER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_TRANSFER, VIEW_ONLY),
                List.of(PERM_PERMIT_TRANSFER_B_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_TRANSFER, EXECUTE),
                List.of(PERM_PERMIT_TRANSFER_B_REVIEW_VIEW_TASK, PERM_PERMIT_TRANSFER_B_REVIEW_EXECUTE_TASK));

        // PEER_REVIEW_PERMIT_TRANSFER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_TRANSFER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_TRANSFER, VIEW_ONLY),
                List.of(PERM_PERMIT_TRANSFER_B_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_TRANSFER, EXECUTE),
                List.of(PERM_PERMIT_TRANSFER_B_PEER_REVIEW_VIEW_TASK, PERM_PERMIT_TRANSFER_B_PEER_REVIEW_EXECUTE_TASK));

        // SUBMIT_PERMIT_BATCH_REISSUE
		permissionGroupLevelsConfig.put(
				new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_BATCH_REISSUE, NONE),
				List.of());
		permissionGroupLevelsConfig.put(
				new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_BATCH_REISSUE, VIEW_ONLY),
				List.of(PERM_PERMIT_BATCH_REISSUE_SUBMIT_VIEW_TASK));
		permissionGroupLevelsConfig.put(
				new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_BATCH_REISSUE, EXECUTE),
				List.of(PERM_PERMIT_BATCH_REISSUE_SUBMIT_VIEW_TASK,
						PERM_PERMIT_BATCH_REISSUE_SUBMIT_EXECUTE_TASK));

        // REVIEW_AER
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_AER, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_AER, VIEW_ONLY),
                        List.of(PERM_AER_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_AER, EXECUTE),
                        List.of(PERM_AER_APPLICATION_REVIEW_VIEW_TASK,
                                PERM_AER_APPLICATION_REVIEW_EXECUTE_TASK));

        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MARK_NOT_REQUIRED_AER, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MARK_NOT_REQUIRED_AER, EXECUTE),
                        List.of(PERM_AER_APPLICATION_MARK_NOT_REQUIRED_EXECUTE_TASK));


        // REVIEW_VIR
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_VIR, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_VIR, VIEW_ONLY),
                        List.of(PERM_VIR_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_VIR, EXECUTE),
                        List.of(PERM_VIR_APPLICATION_REVIEW_VIEW_TASK,
                                PERM_VIR_APPLICATION_REVIEW_EXECUTE_TASK));

        // REVIEW_AIR
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AIR, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AIR, VIEW_ONLY),
                List.of(PERM_AIR_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AIR, EXECUTE),
                List.of(PERM_AIR_APPLICATION_REVIEW_VIEW_TASK,
                    PERM_AIR_APPLICATION_REVIEW_EXECUTE_TASK)
            );

        // DRE SUBMIT
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_DRE, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_DRE, VIEW_ONLY),
                List.of(PERM_DRE_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_DRE, EXECUTE),
                List.of(PERM_DRE_APPLICATION_SUBMIT_VIEW_TASK, PERM_DRE_APPLICATION_SUBMIT_EXECUTE_TASK));

        // DRE PEER_REVIEW
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_DRE, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_DRE, VIEW_ONLY),
                List.of(PERM_DRE_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_DRE, EXECUTE),
                List.of(PERM_DRE_PEER_REVIEW_VIEW_TASK, PERM_DRE_PEER_REVIEW_EXECUTE_TASK));

        // SUBMIT_NON_COMPLIANCE
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_NON_COMPLIANCE, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_NON_COMPLIANCE, VIEW_ONLY),
                List.of(PERM_NON_COMPLIANCE_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_NON_COMPLIANCE, EXECUTE),
                List.of(PERM_NON_COMPLIANCE_SUBMIT_VIEW_TASK, PERM_NON_COMPLIANCE_SUBMIT_EXECUTE_TASK));

        // PEER_REVIEW_NON_COMPLIANCE
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_NON_COMPLIANCE, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_NON_COMPLIANCE, VIEW_ONLY),
                List.of(PERM_NON_COMPLIANCE_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_NON_COMPLIANCE, EXECUTE),
                List.of(PERM_NON_COMPLIANCE_PEER_REVIEW_VIEW_TASK, PERM_NON_COMPLIANCE_PEER_REVIEW_EXECUTE_TASK));

        // REVIEW_NER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_NER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_NER, VIEW_ONLY),
                List.of(PERM_NER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_NER, EXECUTE),
                List.of(PERM_NER_REVIEW_VIEW_TASK, PERM_NER_REVIEW_EXECUTE_TASK));

        // PEER_REVIEW_NER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_NER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_NER, VIEW_ONLY),
                List.of(PERM_NER_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_NER, EXECUTE),
                List.of(PERM_NER_PEER_REVIEW_VIEW_TASK, PERM_NER_PEER_REVIEW_EXECUTE_TASK));

        // SUBMIT_WITHOLDING_OF_ALLOWANCES
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_WITHHOLDING_OF_ALLOWANCES, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_WITHHOLDING_OF_ALLOWANCES, VIEW_ONLY),
                List.of(PERM_WITHHOLDING_OF_ALLOWANCES_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_WITHHOLDING_OF_ALLOWANCES, EXECUTE),
                List.of(PERM_WITHHOLDING_OF_ALLOWANCES_SUBMIT_VIEW_TASK,
                    PERM_WITHHOLDING_OF_ALLOWANCES_SUBMIT_EXECUTE_TASK));

        //PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, VIEW_ONLY),
                        List.of(PERM_WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES, EXECUTE),
                        List.of(PERM_WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK,
                                PERM_WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_EXECUTE_TASK));

        // SUBMIT_RETURN_OF_ALLOWANCES
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_RETURN_OF_ALLOWANCES, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_RETURN_OF_ALLOWANCES, VIEW_ONLY),
                List.of(PERM_RETURN_OF_ALLOWANCES_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_RETURN_OF_ALLOWANCES, EXECUTE),
                List.of(PERM_RETURN_OF_ALLOWANCES_SUBMIT_VIEW_TASK,
                    PERM_RETURN_OF_ALLOWANCES_SUBMIT_EXECUTE_TASK));

        //PEER_REVIEW_RETURN_OF_ALLOWANCES
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_RETURN_OF_ALLOWANCES, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_RETURN_OF_ALLOWANCES, VIEW_ONLY),
                List.of(PERM_RETURN_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_RETURN_OF_ALLOWANCES, EXECUTE),
                List.of(PERM_RETURN_OF_ALLOWANCES_PEER_REVIEW_VIEW_TASK,
                    PERM_RETURN_OF_ALLOWANCES_PEER_REVIEW_EXECUTE_TASK));

        // DOAL SUBMIT
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_DOAL, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_DOAL, VIEW_ONLY),
                        List.of(PERM_DOAL_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_DOAL, EXECUTE),
                        List.of(PERM_DOAL_APPLICATION_SUBMIT_VIEW_TASK, PERM_DOAL_APPLICATION_SUBMIT_EXECUTE_TASK));

        // DOAL PEER_REVIEW
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_DOAL, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_DOAL, VIEW_ONLY),
                        List.of(PERM_DOAL_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_DOAL, EXECUTE),
                        List.of(PERM_DOAL_PEER_REVIEW_VIEW_TASK, PERM_DOAL_PEER_REVIEW_EXECUTE_TASK));


        //installation inspection
        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(SUBMIT_INSTALLATION_AUDIT, NONE), List.of());
        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(SUBMIT_INSTALLATION_AUDIT, VIEW_ONLY),
                        List.of(PERM_INSTALLATION_AUDIT_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_INSTALLATION_AUDIT, EXECUTE),
                        List.of(PERM_INSTALLATION_AUDIT_APPLICATION_SUBMIT_VIEW_TASK, PERM_INSTALLATION_AUDIT_APPLICATION_SUBMIT_EXECUTE_TASK));

        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(PEER_REVIEW_INSTALLATION_AUDIT, NONE), List.of());
        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(PEER_REVIEW_INSTALLATION_AUDIT, VIEW_ONLY),
                List.of(PERM_INSTALLATION_AUDIT_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_INSTALLATION_AUDIT, EXECUTE),
                        List.of(PERM_INSTALLATION_AUDIT_PEER_REVIEW_VIEW_TASK, PERM_INSTALLATION_AUDIT_PEER_REVIEW_EXECUTE_TASK));

        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(SUBMIT_INSTALLATION_ONSITE_INSPECTION, NONE), List.of());
        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(SUBMIT_INSTALLATION_ONSITE_INSPECTION, VIEW_ONLY),
                List.of(PERM_INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(SUBMIT_INSTALLATION_ONSITE_INSPECTION, EXECUTE),
                List.of(PERM_INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_EXECUTE_TASK,PERM_INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_VIEW_TASK));

        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(PEER_REVIEW_INSTALLATION_ONSITE_INSPECTION, NONE), List.of());
        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(PEER_REVIEW_INSTALLATION_ONSITE_INSPECTION, VIEW_ONLY),
                List.of(PERM_INSTALLATION_ONSITE_INSPECTION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_INSTALLATION_ONSITE_INSPECTION, EXECUTE),
                        List.of(PERM_INSTALLATION_ONSITE_INSPECTION_PEER_REVIEW_VIEW_TASK, PERM_INSTALLATION_ONSITE_INSPECTION_PEER_REVIEW_EXECUTE_TASK));


        //BDR
        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(SUBMIT_BDR_REVIEW, NONE), List.of());
        permissionGroupLevelsConfig.put(new RegulatorPermissionGroupLevel(SUBMIT_BDR_REVIEW, VIEW_ONLY),
                        List.of(PERM_BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_BDR_REVIEW, EXECUTE),
                        List.of(PERM_BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_VIEW_TASK, PERM_BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_EXECUTE_TASK));

        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(PEER_REVIEW_BDR, NONE),
                List.of());
        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(PEER_REVIEW_BDR, VIEW_ONLY),
                List.of(PERM_BDR_APPLICATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(PEER_REVIEW_BDR, EXECUTE),
                List.of(PERM_BDR_APPLICATION_PEER_REVIEW_VIEW_TASK,
                        PERM_BDR_APPLICATION_PEER_REVIEW_EXECUTE_TASK));

        //Permanent Cessation
        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(SUBMIT_PERMANENT_CESSATION, NONE),
                List.of());
        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(SUBMIT_PERMANENT_CESSATION, VIEW_ONLY),
                List.of(PERM_PERMANENT_CESSATION_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(SUBMIT_PERMANENT_CESSATION, EXECUTE),
                List.of(PERM_PERMANENT_CESSATION_APPLICATION_SUBMIT_VIEW_TASK, PERM_PERMANENT_CESSATION_APPLICATION_SUBMIT_EXECUTE_TASK));

        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMANENT_CESSATION, NONE),
                List.of());
        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMANENT_CESSATION, VIEW_ONLY),
                List.of(PERM_PERMANENT_CESSATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig.put(
                new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMANENT_CESSATION, EXECUTE),
                List.of(PERM_PERMANENT_CESSATION_PEER_REVIEW_VIEW_TASK, PERM_PERMANENT_CESSATION_PEER_REVIEW_EXECUTE_TASK));


        // REVIEW_EMP_APPLICATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_EMP_APPLICATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_EMP_APPLICATION, VIEW_ONLY),
                List.of(PERM_EMP_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_EMP_APPLICATION, EXECUTE),
                List.of(PERM_EMP_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK, PERM_EMP_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK));

        // PEER_REVIEW_EMP_APPLICATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_EMP_APPLICATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_EMP_APPLICATION, VIEW_ONLY),
                List.of(PERM_EMP_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_EMP_APPLICATION, EXECUTE),
                List.of(PERM_EMP_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK,
                        PERM_EMP_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK));

        // SUBMIT_REVIEW_EMP_VARIATION
        permissionGroupLevelsConfig
        		.put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_EMP_VARIATION, NONE), List.of());
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_EMP_VARIATION, VIEW_ONLY),
		                List.of(PERM_EMP_VARIATION_SUBMIT_REVIEW_VIEW_TASK));
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_EMP_VARIATION, EXECUTE),
		                List.of(PERM_EMP_VARIATION_SUBMIT_REVIEW_VIEW_TASK,
		                		PERM_EMP_VARIATION_SUBMIT_REVIEW_EXECUTE_TASK));
		
		// PEER_REVIEW_EMP_VARIATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_EMP_VARIATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_EMP_VARIATION, VIEW_ONLY),
                List.of(PERM_EMP_VARIATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_EMP_VARIATION, EXECUTE),
                List.of(PERM_EMP_VARIATION_PEER_REVIEW_VIEW_TASK,
                		PERM_EMP_VARIATION_PEER_REVIEW_EXECUTE_TASK));

        // AVIATION DRE SUBMIT
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_DRE, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_DRE, VIEW_ONLY),
                        List.of(PERM_AVIATION_DRE_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_DRE, EXECUTE),
                        List.of(PERM_AVIATION_DRE_APPLICATION_SUBMIT_VIEW_TASK, PERM_AVIATION_DRE_APPLICATION_SUBMIT_EXECUTE_TASK));

        // AVIATION DOE CORSIA SUBMIT
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_DOE_CORSIA, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_DOE_CORSIA, VIEW_ONLY),
                        List.of(PERM_AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_DOE_CORSIA, EXECUTE),
                        List.of(PERM_AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_VIEW_TASK, PERM_AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_EXECUTE_TASK));

        // AVIATION DOE CORSIA PEER_REVIEW
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_DOE_CORSIA, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_DOE_CORSIA, VIEW_ONLY),
                List.of(PERM_AVIATION_DOE_CORSIA_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_DOE_CORSIA, EXECUTE),
                List.of(PERM_AVIATION_DOE_CORSIA_PEER_REVIEW_VIEW_TASK, PERM_AVIATION_DOE_CORSIA_PEER_REVIEW_EXECUTE_TASK));


        // AVIATION_ACCOUNT_CLOSURE
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(AVIATION_ACCOUNT_CLOSURE, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(AVIATION_ACCOUNT_CLOSURE, VIEW_ONLY),
                List.of(PERM_AVIATION_ACCOUNT_CLOSURE_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(AVIATION_ACCOUNT_CLOSURE, EXECUTE),
                List.of(PERM_AVIATION_ACCOUNT_CLOSURE_SUBMIT_VIEW_TASK, PERM_AVIATION_ACCOUNT_CLOSURE_SUBMIT_EXECUTE_TASK));

        // AVIATION DRE PEER_REVIEW
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_DRE, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_DRE, VIEW_ONLY),
                List.of(PERM_AVIATION_DRE_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_DRE, EXECUTE),
                List.of(PERM_AVIATION_DRE_PEER_REVIEW_VIEW_TASK, PERM_AVIATION_DRE_PEER_REVIEW_EXECUTE_TASK));

        // REVIEW_AVIATION_AER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AVIATION_AER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AVIATION_AER, VIEW_ONLY),
                List.of(PERM_AVIATION_AER_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AVIATION_AER, EXECUTE),
                List.of(PERM_AVIATION_AER_APPLICATION_REVIEW_VIEW_TASK,
                    PERM_AVIATION_AER_APPLICATION_REVIEW_EXECUTE_TASK));
        
        // REVIEW_AVIATION_VIR
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AVIATION_VIR, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AVIATION_VIR, VIEW_ONLY),
                List.of(PERM_AVIATION_VIR_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_AVIATION_VIR, EXECUTE),
                List.of(PERM_AVIATION_VIR_APPLICATION_REVIEW_VIEW_TASK,
                        PERM_AVIATION_VIR_APPLICATION_REVIEW_EXECUTE_TASK));

        // AVIATION_SUBMIT_NON_COMPLIANCE
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_NON_COMPLIANCE, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_NON_COMPLIANCE, VIEW_ONLY),
                List.of(PERM_AVIATION_NON_COMPLIANCE_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_AVIATION_NON_COMPLIANCE, EXECUTE),
                List.of(PERM_AVIATION_NON_COMPLIANCE_SUBMIT_VIEW_TASK, PERM_AVIATION_NON_COMPLIANCE_SUBMIT_EXECUTE_TASK));

        // AVIATION_PEER_REVIEW_NON_COMPLIANCE
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_NON_COMPLIANCE, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_NON_COMPLIANCE, VIEW_ONLY),
                List.of(PERM_AVIATION_NON_COMPLIANCE_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_NON_COMPLIANCE, EXECUTE),
                List.of(PERM_AVIATION_NON_COMPLIANCE_PEER_REVIEW_VIEW_TASK, PERM_AVIATION_NON_COMPLIANCE_PEER_REVIEW_EXECUTE_TASK));


        // AVIATION AER ANNUAL OFFSETTING
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(AVIATION_AER_ANNUAL_OFFSETTING, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(AVIATION_AER_ANNUAL_OFFSETTING, VIEW_ONLY),
                        List.of(PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(AVIATION_AER_ANNUAL_OFFSETTING, EXECUTE),
                        List.of(PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_VIEW_TASK,
                                PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_EXECUTE_TASK));

        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_AER_ANNUAL_OFFSETTING, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_AER_ANNUAL_OFFSETTING, VIEW_ONLY),
                        List.of(PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_AER_ANNUAL_OFFSETTING, EXECUTE),
                        List.of(PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_VIEW_TASK,
                                PERM_AVIATION_AER_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_EXECUTE_TASK));

        // AVIATION AER 3 YEAR PERIOD OFFSETTING
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(AVIATION_AER_3YEAR_PERIOD_OFFSETTING, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(AVIATION_AER_3YEAR_PERIOD_OFFSETTING, VIEW_ONLY),
                        List.of(PERM_AVIATION_AER_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(AVIATION_AER_3YEAR_PERIOD_OFFSETTING, EXECUTE),
                        List.of(PERM_AVIATION_AER_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_VIEW_TASK,
                                PERM_AVIATION_AER_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_EXECUTE_TASK));

        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_AER_3YEAR_PERIOD_OFFSETTING, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_AER_3YEAR_PERIOD_OFFSETTING, VIEW_ONLY),
                        List.of(PERM_AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_AVIATION_AER_3YEAR_PERIOD_OFFSETTING, EXECUTE),
                        List.of(PERM_AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_VIEW_TASK,
                                PERM_AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_EXECUTE_TASK));

        // SUBMIT_EMP_BATCH_REISSUE
		permissionGroupLevelsConfig.put(
				new RegulatorPermissionGroupLevel(SUBMIT_EMP_BATCH_REISSUE, NONE),
				List.of());
		permissionGroupLevelsConfig.put(
				new RegulatorPermissionGroupLevel(SUBMIT_EMP_BATCH_REISSUE, VIEW_ONLY),
				List.of(PERM_EMP_BATCH_REISSUE_SUBMIT_VIEW_TASK));
		permissionGroupLevelsConfig.put(
				new RegulatorPermissionGroupLevel(SUBMIT_EMP_BATCH_REISSUE, EXECUTE),
				List.of(PERM_EMP_BATCH_REISSUE_SUBMIT_VIEW_TASK,
						PERM_EMP_BATCH_REISSUE_SUBMIT_EXECUTE_TASK));

    }


    @Override
    public Map<RegulatorPermissionGroupLevel, List<String>> getPermissionGroupLevelsConfig() {
        return permissionGroupLevelsConfig;
    }
}
