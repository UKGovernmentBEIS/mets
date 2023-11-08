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
public class AirImprovementMeasurement extends AirImprovement {

    @NotEmpty
    @Builder.Default
    private List<String> sourceStreamReferences = new ArrayList<>();

    @NotNull
    private String emissionPoint;
    
    @NotNull
    private String parameter;

    @NotNull
    private String tier;
}
