package uk.gov.pmrv.api.allowance.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChangeType {

    CESSATION("Cessation"),
    INCREASE("Increase"),
    DECREASE("Decrease"),
    RETURN_TO_HISTORICAL_ACTIVITY_LEVELS_HAL("Return to Historical Activity Levels (HAL)"),
    NER_ALLOCATION_FOR_YEAR_0_BASED_ON_ACTIVITY_LEVEL_AL_IN_YEAR_0("NER: Allocation for year 0 based on Activity Level (AL) in year 0"),
    NER_ALLOCATION_FOR_YEAR_0_PLUS_1_BASED_ON_ACTIVITY_LEVEL_AL_IN_YEAR_0_PLUS_1("NER: Allocation for year 0+1 based on Activity Level (AL) in year 0+1"),
    NER_ALLOCATION_FOR_YEAR_0_PLUS_2_BASED_ON_THE_HISTORICAL_ACTIVITY_LEVEL_HAL("NER: Allocation for year 0+2 based on the Historical Activity Level (HAL)"),
    REGULATOR_REJECTS_ADJUSTMENT("Regulator rejects adjustment"),
    NO_CHANGE("No change"),
    OTHER("Other");

    private final String description;
}
