package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums;

import lombok.Getter;

@Getter
public enum NERViolation {

    INVALID_REVIEW_OPINION("Request action type does not match NER review opinion."),
    INVALID_REGULATOR_REVIEW_OUTCOME("Regulator review outcome should not be null");

    private final String message;

    NERViolation(String message) {this.message = message;}
}
