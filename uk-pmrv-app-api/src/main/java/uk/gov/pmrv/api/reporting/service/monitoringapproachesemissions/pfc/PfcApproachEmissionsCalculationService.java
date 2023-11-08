package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.pfc;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;
import uk.gov.pmrv.api.reporting.service.ApproachEmissionsCalculationService;

import java.math.BigDecimal;

@Service
public class PfcApproachEmissionsCalculationService implements ApproachEmissionsCalculationService {

    @Override
    public BigDecimal getTotalEmissions(AerMonitoringApproachEmissions approachEmissions) {
        return ((CalculationOfPfcEmissions) approachEmissions).getSourceStreamEmissions().stream()
            .map(this::getSourceStreamTotalReportableEmissions)
            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    @Override
    public MonitoringApproachType getType() {
        return MonitoringApproachType.CALCULATION_PFC;
    }

    private BigDecimal getSourceStreamTotalReportableEmissions(PfcSourceStreamEmission pfcSourceStreamEmission) {
        return Boolean.TRUE.equals(pfcSourceStreamEmission.getCalculationCorrect()) ?
            pfcSourceStreamEmission.getReportableEmissions()
            : pfcSourceStreamEmission.getProvidedEmissions().getTotalProvidedReportableEmissions();
    }
}
