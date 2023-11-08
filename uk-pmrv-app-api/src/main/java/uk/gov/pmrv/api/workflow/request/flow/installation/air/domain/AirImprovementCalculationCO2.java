package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AirImprovementCalculationCO2 extends AirImprovement {

    @NotNull
    private String sourceStreamReference;

    @NotNull
    private String parameter;
    
    @NotNull
    private String tier;
}
