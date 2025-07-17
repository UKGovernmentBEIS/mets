package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.EmissionPointBase;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class MeasurementOfCO2EmissionPointCategory extends EmissionPointBase {

    @Valid
    private TransferCO2 transfer;

}
