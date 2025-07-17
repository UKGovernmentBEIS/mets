package uk.gov.pmrv.api.notification.template.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates the various notification template names.
 */
@Getter
@AllArgsConstructor
public enum PmrvNotificationTemplateName {

    //Email Notification Template Names
    EMAIL_CONFIRMATION("EmailConfirmation"),
    USER_ACCOUNT_CREATED("UserAccountCreated"),
    USER_ACCOUNT_ACTIVATION("UserAccountActivation"),
    ACCOUNT_APPLICATION_RECEIVED("AccountApplicationReceived"),
    ACCOUNT_APPLICATION_ACCEPTED("AccountApplicationAccepted"),
    ACCOUNT_APPLICATION_REJECTED("AccountApplicationRejected"),
    INVITATION_TO_OPERATOR_ACCOUNT("InvitationToOperatorAccount"),
    INVITATION_TO_REGULATOR_ACCOUNT("InvitationToRegulatorAccount"),
    INVITATION_TO_VERIFIER_ACCOUNT("InvitationToVerifierAccount"),
    INVITATION_TO_EMITTER_CONTACT("InvitationToEmitterContact"),
    INVITEE_INVITATION_ACCEPTED("InviteeInvitationAccepted"),
    INVITER_INVITATION_ACCEPTED("InviterInvitationAccepted"),
    CHANGE_2FA("Change2FA"),
    RESET_PASSWORD_REQUEST("ResetPasswordRequest"),
    RESET_PASSWORD_CONFIRMATION("ResetPasswordConfirmation"),
    RESET_2FA_CONFIRMATION("Reset2FaConfirmation"),
    
    GENERIC_EMAIL("Generic email template"),
    GENERIC_EXPIRATION_REMINDER("Generic Expiration Reminder Template"),
    
    //System Message Notification Template Names
    ACCOUNT_USERS_SETUP("AccountUsersSetup"),
    NEW_VERIFICATION_BODY_EMITTER("NewVerificationBodyEmitter"),
    VERIFIER_NO_LONGER_AVAILABLE("VerifierNoLongerAvailable"),

    PERMIT_NOTIFICATION_OPERATOR_RESPONSE("Permit Notification Operator Response"),

    VIR_NOTIFICATION_OPERATOR_RESPONSE("Verifier Improvement Report Operator Response"),
    VIR_NOTIFICATION_OPERATOR_MISSES_DEADLINE("Verifier Improvement Report Operator misses a deadline"),

    AIR_NOTIFICATION_OPERATOR_RESPONSE("Annual Improvement Report Operator Response"),
    AIR_NOTIFICATION_OPERATOR_MISSES_DEADLINE("Annual Improvement Report Operator misses a deadline"),

    USER_FEEDBACK("UserFeedbackForService"),

    EMAIL_ASSIGNED_TASK("EmailAssignedTask"),

    INSTALLATION_INSPECTION_NOTIFICATION_OPERATOR_RESPONSE("Installation Inspection Operator Response"),

    REGISTRY_INTEGRATION_RESPONSE_ERROR_ACTION_TEMPLATE("Registry integration email template consume registry error"),
    REGISTRY_INTEGRATION_RESPONSE_ERROR_INFO_TEMPLATE("Registry integration email template consume registry error info template"),
    REGISTRY_INTEGRATION_MISSING_REGISTRY_ID("Registry integration email template missing registry id error"),
    REGISTRY_INTEGRATION_MISSING_GHGE_HSE_FLAG("Registry integration email template missing ghge - hse flag error"),

    BDR_COMPLETED("BDR completed")
    ;


    /** The name. */
    private final String name;

    /** Maps keys representing values of name property to NotificationTemplate values . */
    private static final Map<String, PmrvNotificationTemplateName> BY_NAME = new HashMap<>();

    static {
        for (PmrvNotificationTemplateName e : values()) {
            BY_NAME.put(e.name, e);
        }
    }

    /**
     * Retrieves PmrvNotificationTemplateName value from the provided name.
     * @param name name attribute
     * @return PmrvNotificationTemplateName value
     */
    public static PmrvNotificationTemplateName getEnumValueFromName(String name) {
        return BY_NAME.get(name);
    }
}
