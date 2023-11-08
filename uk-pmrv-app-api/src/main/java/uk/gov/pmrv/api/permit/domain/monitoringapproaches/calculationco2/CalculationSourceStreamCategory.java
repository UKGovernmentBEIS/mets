package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.SourceStreamCategoryBase;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationSourceStreamCategory extends SourceStreamCategoryBase {

    @NotNull
    private CalculationMethod calculationMethod;

    @Valid
    private TransferCO2 transfer;
}
