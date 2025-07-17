package uk.gov.pmrv.api.user.core.domain.enumeration;

/**
 * The status result of a user invitation action
 */
public enum UserInvitationStatus {
	
	ACCEPTED,
	PENDING_TO_REGISTERED_SET_REGISTER_FORM,
	PENDING_TO_REGISTERED_SET_REGISTER_FORM_NO_PASSWORD,
	PENDING_TO_REGISTERED_SET_PASSWORD_ONLY,
	ALREADY_REGISTERED,
	ALREADY_REGISTERED_SET_PASSWORD_ONLY,
	
}
