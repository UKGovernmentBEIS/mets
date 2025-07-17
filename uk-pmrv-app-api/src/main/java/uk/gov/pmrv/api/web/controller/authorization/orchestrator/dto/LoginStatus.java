package uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto;

public enum LoginStatus {

	NO_ROLE_TYPE, //neither manually registered nor invited
    ENABLED,
    DISABLED,
    ACCEPTED,
    TEMP_DISABLED,
    NO_AUTHORITY
}
