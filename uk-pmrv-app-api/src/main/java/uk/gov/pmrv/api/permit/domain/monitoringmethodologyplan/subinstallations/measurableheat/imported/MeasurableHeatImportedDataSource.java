package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasurableHeatImportedDataSource {

    @NotBlank
    @Size(max = 255)
    private String dataSourceNo;

    @Builder.Default
    @Size(min =1, max = 5)
    private Map<@NotNull MeasurableHeatImportedType, @NotNull MeasurableHeatImportedDataSourceDetails> measurableHeatImportedActivityDetails = new HashMap<>();
}
