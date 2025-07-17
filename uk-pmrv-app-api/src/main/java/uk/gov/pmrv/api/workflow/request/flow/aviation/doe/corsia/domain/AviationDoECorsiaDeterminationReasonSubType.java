package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AviationDoECorsiaDeterminationReasonSubType {

    CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT),
    CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT),
    CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
    ;


    private final AviationDoECorsiaDeterminationReasonType type;
}
