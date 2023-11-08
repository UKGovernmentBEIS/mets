package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.fallback;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.service.ApproachEmissionsCalculationService;

import java.math.BigDecimal;

@Service
public class FallbackApproachEmissionsCalculationService implements ApproachEmissionsCalculationService {

    @Override
    public MonitoringApproachType getType() {
        return MonitoringApproachType.FALLBACK;
    }

    @Override
    public BigDecimal getTotalEmissions(AerMonitoringApproachEmissions approachEmissions) {
        FallbackEmissions fallbackEmissions = (FallbackEmissions) approachEmissions;
        FallbackEmissionsCalculationParamsDTO fallbackEmissionsCalculationParamsDTO =
            FallbackEmissionsCalculationParamsDTO.builder()
                .containsBiomass(((FallbackEmissions) approachEmissions).getBiomass().getContains())
                .totalFossilEmissions(fallbackEmissions.getTotalFossilEmissions())
                .totalNonSustainableBiomassEmissions(fallbackEmissions.getBiomass().getTotalNonSustainableBiomassEmissions())
                .build();
        return calculateEmissions(fallbackEmissionsCalculationParamsDTO).getReportableEmissions();
    }

    public FallbackEmissionsCalculationDTO calculateEmissions(FallbackEmissionsCalculationParamsDTO emissionsCalculationParams) {
        FallbackEmissionsCalculationDTO.FallbackEmissionsCalculationDTOBuilder builder =
            FallbackEmissionsCalculationDTO.builder();
        if (emissionsCalculationParams.isContainsBiomass()) {
            builder
                .reportableEmissions(
                    emissionsCalculationParams.getTotalFossilEmissions().add(
                        emissionsCalculationParams.getTotalNonSustainableBiomassEmissions()
                    )
                )
                .build();
        } else {
            builder
                .reportableEmissions(emissionsCalculationParams.getTotalFossilEmissions())
                .build();
        }
        return builder.build();
    }
}
