package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotOverallVerificationReasonType {

    UNCORRECTED_MATERIAL_MISSTATEMENT("An uncorrected material misstatement (individual or in aggregate)"),
    UNCORRECTED_MATERIAL_NON_CONFORMITY("An uncorrected material non-conformity (individual or in aggregate)"),
    DATA_OR_INFORMATION_LIMITATIONS("Limitations in the data or information made available for verification"),
    SCOPE_LIMITATIONS_CLARITY("Limitations of scope due to lack of clarity"),
    SCOPE_LIMITATIONS_MONITORING_PLAN("Limitations of scope of the approved monitoring plan"),
    NOT_APPROVED_MONITORING_PLAN("The monitoring plan is not approved by the competent authority"),
    ANOTHER_REASON("Another reason")
    ;

    private final String description;
}
