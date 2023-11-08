package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataReportable;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaRequestMetadata extends AviationAerRequestMetadata implements RequestMetadataReportable {

    private BigDecimal totalEmissionsOffsettingFlights;

    private BigDecimal totalEmissionsClaimedReductions;
}