package uk.gov.pmrv.api.authorization.rules.domain;

public enum ResourceType {

    ACCOUNT,
    CA,
    VERIFICATION_BODY,
    PERMIT,
    EMP,

    REQUEST,
    REQUEST_TASK,
    REQUEST_ACTION,

    NOTIFICATION_TEMPLATE,
    DOCUMENT_TEMPLATE,
    ACCOUNT_NOTE,
    REQUEST_NOTE,
}
