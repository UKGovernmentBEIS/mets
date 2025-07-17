package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;



@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsia3YearPeriodOffsettingRequestMetadata extends RequestMetadata {

    //added for data consistency
    @NotNull
    private Year year;

    @NotNull
    private String currentAerId;

    @Positive(message = "Must be positive number.")
    private Long periodOffsettingRequirements;


    @Builder.Default
    @Size(min = 3, max = 3, message = "Must contain exactly 3 years.")
    private List<Year> years = new ArrayList<>();

    private Boolean operatorHaveOffsettingRequirements;
}
