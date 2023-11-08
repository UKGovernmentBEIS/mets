package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AviationDreDeterminationReasonType {

    VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER("because you failed to submit a report of aviation emissions by 31 March %s in accordance with Article 33 of the Order."),
    CORRECTING_NON_MATERIAL_MISSTATEMENT("because your annual emissions report for the %s Scheme Year contained a non-material misstatement or misstatements that were not corrected before the issue of the related verification report."),
    IMPOSING_OR_CONSIDERING_IMPOSING_CIVIL_PENALTY_IN_ACCORDANCE_WITH_ORDER("for the purpose of imposing, or considering whether to impose, a civil penalty under Article 47 of the Order.")
    ;

    private final String description;
}
