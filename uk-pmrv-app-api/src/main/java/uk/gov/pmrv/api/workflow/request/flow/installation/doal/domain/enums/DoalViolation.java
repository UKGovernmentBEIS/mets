package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums;

import lombok.Getter;

@Getter
public enum DoalViolation {

    ATTACHMENT_NOT_FOUND("Attachment not found"),
    ATTACHMENT_NOT_REFERENCED("Attachment is not referenced in DOAL"),
    INVALID_ARTICLE_REASONS("Proceed to authority determination article reasons are not valid"),
    INVALID_PRELIMINARY_ALLOCATIONS("Preliminary allocations submitted are not valid"),
    INVALID_TOTAL_ALLOCATIONS_PER_YEAR("Proceed to authority determination total allocations per year are not valid");

    private final String message;

    DoalViolation(String message) {
        this.message = message;
    }
}
