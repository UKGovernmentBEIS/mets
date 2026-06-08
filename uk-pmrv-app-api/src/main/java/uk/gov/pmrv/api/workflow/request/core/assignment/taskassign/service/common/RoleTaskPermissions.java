package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.common;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Arrays;
import java.util.Set;

public enum RoleTaskPermissions {

    /* INSTALLATION */

    REGULATOR_INSTALLATION(
            RoleTypeConstants.REGULATOR,
            AccountType.INSTALLATION,
            Set.of(
                    RequestTaskType.AER_APPLICATION_REVIEW,
                    RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                    RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW,
                    RequestTaskType.PERMIT_ISSUANCE_TRACK_PAYMENT,
                    RequestTaskType.PERMIT_ISSUANCE_CONFIRM_PAYMENT,
                    RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW,
                    RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_REVIEW,
                    RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS,
                    RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW,
                    RequestTaskType.PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP,
                    RequestTaskType.PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE,
                    RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW,
                    RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT,
                    RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW,
                    RequestTaskType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW,
                    RequestTaskType.PERMIT_TRANSFER_A_CONFIRM_PAYMENT,
                    RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_REVIEW,
                    RequestTaskType.PERMIT_TRANSFER_B_WAIT_FOR_AMENDS,
                    RequestTaskType.PERMIT_TRANSFER_B_WAIT_FOR_RFI_RESPONSE,
                    RequestTaskType.PERMIT_TRANSFER_B_CONFIRM_PAYMENT,
                    RequestTaskType.NER_APPLICATION_REVIEW,
                    RequestTaskType.NER_TRACK_PAYMENT,
                    RequestTaskType.NER_CONFIRM_PAYMENT,
                    RequestTaskType.NER_APPLICATION_PEER_REVIEW,
                    RequestTaskType.NER_WAIT_FOR_AMENDS
            )
    ),
    OPERATOR_INSTALLATION(
            RoleTypeConstants.OPERATOR,
            AccountType.INSTALLATION,
            Set.of(
                    RequestTaskType.PERMIT_VARIATION_APPLICATION_SUBMIT,
                    RequestTaskType.PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW,
                    RequestTaskType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.PERMIT_VARIATION_WAIT_FOR_REVIEW,
                    RequestTaskType.PERMIT_VARIATION_RFI_RESPONSE_SUBMIT,
                    RequestTaskType.AER_WAIT_FOR_VERIFICATION,
                    RequestTaskType.AER_APPLICATION_SUBMIT,
                    RequestTaskType.AER_WAIT_FOR_REVIEW,
                    RequestTaskType.AER_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.AER_AMEND_WAIT_FOR_VERIFICATION,
                    RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_REVIEW,
                    RequestTaskType.PERMIT_ISSUANCE_MAKE_PAYMENT,
                    RequestTaskType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT,
                    RequestTaskType.NER_APPLICATION_SUBMIT,
                    RequestTaskType.NER_WAIT_FOR_VERIFICATION,
                    RequestTaskType.NER_WAIT_FOR_REVIEW,
                    RequestTaskType.PERMIT_TRANSFER_A_APPLICATION_SUBMIT,
                    RequestTaskType.PERMIT_TRANSFER_A_WAIT_FOR_TRANSFER,
                    RequestTaskType.PERMIT_TRANSFER_A_MAKE_PAYMENT,
                    RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_SUBMIT,
                    RequestTaskType.PERMIT_TRANSFER_B_WAIT_FOR_REVIEW,
                    RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.PERMIT_TRANSFER_B_RFI_RESPONSE_SUBMIT,
                    RequestTaskType.PERMIT_TRANSFER_B_MAKE_PAYMENT,
                    RequestTaskType.PERMIT_NOTIFICATION_WAIT_FOR_REVIEW,
                    RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT,
                    RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP,
                    RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW,
                    RequestTaskType.NER_APPLICATION_AMENDS_SUBMIT
            )
    ),
    VERIFIER_INSTALLATION(
            RoleTypeConstants.VERIFIER,
            AccountType.INSTALLATION,
            Set.of(
                    RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT,
                    RequestTaskType.AER_AMEND_APPLICATION_VERIFICATION_SUBMIT,
                    RequestTaskType.NER_APPLICATION_VERIFICATION_SUBMIT
            )
    ),

    /* AVIATION*/

    REGULATOR_AVIATION(
            RoleTypeConstants.REGULATOR,
            AccountType.AVIATION,
            Set.of(
                    RequestTaskType.AVIATION_AER_UKETS_APPLICATION_REVIEW,
                    RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_REVIEW,
                    RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW,
                    RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW,
                    RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT,
                    RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT,
                    RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW,
                    RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW,
                    RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW,
                    RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW,
                    RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW,
                    RequestTaskType.EMP_ISSUANCE_UKETS_TRACK_PAYMENT,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_TRACK_PAYMENT,
                    RequestTaskType.EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_CONFIRM_PAYMENT,
                    RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW
            )
    ),
    OPERATOR_AVIATION(
            RoleTypeConstants.OPERATOR,
            AccountType.AVIATION,
            Set.of(
                    RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_SUBMIT,
                    RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT,
                    RequestTaskType.EMP_VARIATION_UKETS_WAIT_FOR_REVIEW,
                    RequestTaskType.EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW,
                    RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.EMP_VARIATION_UKETS_RFI_RESPONSE_SUBMIT,
                    RequestTaskType.EMP_VARIATION_CORSIA_RFI_RESPONSE_SUBMIT,
                    RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT,
                    RequestTaskType.EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_WAIT_FOR_REVIEW,
                    RequestTaskType.EMP_ISSUANCE_UKETS_MAKE_PAYMENT,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_MAKE_PAYMENT,
                    RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.EMP_ISSUANCE_UKETS_RFI_RESPONSE_SUBMIT,
                    RequestTaskType.EMP_ISSUANCE_CORSIA_RFI_RESPONSE_SUBMIT,
                    RequestTaskType.AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION,
                    RequestTaskType.AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION,
                    RequestTaskType.AVIATION_AER_UKETS_APPLICATION_SUBMIT,
                    RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_SUBMIT,
                    RequestTaskType.AVIATION_AER_UKETS_WAIT_FOR_REVIEW,
                    RequestTaskType.AVIATION_AER_CORSIA_WAIT_FOR_REVIEW,
                    RequestTaskType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT,
                    RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT
            )
    ),
    VERIFIER_AVIATION(
            RoleTypeConstants.VERIFIER,
            AccountType.AVIATION,
            Set.of(
                    RequestTaskType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT,
                    RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT,
                    RequestTaskType.AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT,
                    RequestTaskType.AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT
            )
    );

    private final String role;
    private final AccountType accountType;
    private final Set<RequestTaskType> allowedTasks;

    RoleTaskPermissions(String role, AccountType accountType, Set<RequestTaskType> allowedTasks) {
        this.role = role;
        this.accountType = accountType;
        this.allowedTasks = allowedTasks;
    }

    public static boolean isAllowed(String role, AccountType accountType, RequestTaskType type) {
        return Arrays.stream(values())
                .filter(p -> p.role.equals(role) && p.accountType == accountType)
                .findFirst()
                .map(p -> p.allowedTasks.contains(type))
                .orElse(false);
    }
}
