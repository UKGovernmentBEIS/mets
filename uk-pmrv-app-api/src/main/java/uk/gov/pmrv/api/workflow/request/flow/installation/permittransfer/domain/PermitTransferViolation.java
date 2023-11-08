package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import lombok.Getter;

@Getter
public enum PermitTransferViolation {

    ATTACHMENT_NOT_FOUND("Attachment not found"),
    ATTACHMENT_NOT_REFERENCED("Attachment is not referenced in permit transfer"),
    AER_IN_PROGRESS_NEEDS_ASSIGNMENT("Assign the aer in progress"),
    INVALID_TRANSFER_CODE("The transfer code is not valid");

    private final String message;

    PermitTransferViolation(String message) {
        this.message = message;
    }

}
