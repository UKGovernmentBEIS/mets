package uk.gov.pmrv.api.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SourceStreamCalculationParametersInfo {

    @Builder.Default
    private Set<CalculationParameterType> applicableTypes = new HashSet<>();

    @Builder.Default
    private List<CalculationParameterMeasurementUnits> measurementUnitsCombinations = new ArrayList<>();

}
