package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums;

import lombok.Getter;

@Getter
public enum NerViolation {

    ATTACHMENT_NOT_FOUND("Attachment not found"),
    ATTACHMENT_NOT_REFERENCED("Attachment is not referenced in NER");

    private final String message;

    NerViolation(String message) {
        this.message = message;
    }
}
