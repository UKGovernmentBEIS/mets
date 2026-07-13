package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.n2o;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.reporting.domain.GlobalWarmingPotential;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementN2OEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.MeasurementEmissionsCalculationService;

import java.math.BigDecimal;

@Validated
@Service
@RequiredArgsConstructor
public class MeasurementN2OEmissionsCalculationService {

    private final MeasurementEmissionsCalculationService measurementEmissionsCalculationService;

    public MeasurementEmissionsCalculationDTO calculateEmissions(@Valid @NotNull MeasurementN2OEmissionsCalculationParamsDTO emissionsCalculationParamsDTO) {
        BigDecimal globalWarmingPotential = GlobalWarmingPotential.N2O.getValue(emissionsCalculationParamsDTO.getReportingYear());
        return measurementEmissionsCalculationService.calculateEmissions(
            globalWarmingPotential,
            emissionsCalculationParamsDTO
        );
    }

}
