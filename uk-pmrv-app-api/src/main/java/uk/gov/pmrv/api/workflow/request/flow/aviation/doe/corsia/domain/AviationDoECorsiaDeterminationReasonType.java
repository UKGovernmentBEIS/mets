package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AviationDoECorsiaDeterminationReasonType {

    VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED("A verified emissions report has not been submitted"),
    CORRECTIONS_TO_A_VERIFIED_REPORT("We are making corrections to a verified report"),
    ;


    private final String description;
}
