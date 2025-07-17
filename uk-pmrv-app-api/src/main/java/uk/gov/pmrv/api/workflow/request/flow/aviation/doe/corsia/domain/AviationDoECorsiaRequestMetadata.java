package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataReportable;

import java.math.BigDecimal;
import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationDoECorsiaRequestMetadata extends RequestMetadata implements RequestMetadataReportable {

    @NotNull
    @PastOrPresent
    private Year year;

    @NotNull
    @Positive
    private BigDecimal emissions;

    private BigDecimal totalEmissionsOffsettingFlights;

    private BigDecimal totalEmissionsClaimedReductions;

    private boolean isExempted;
}
