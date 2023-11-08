package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AirImprovementCalculationPFC extends AirImprovement {

    @NotNull
    private String sourceStreamReference;

    @NotEmpty
    @Builder.Default
    private List<String> emissionPoints = new ArrayList<>();

    @NotNull
    private String parameter;

    @NotNull
    private String tier;
}
