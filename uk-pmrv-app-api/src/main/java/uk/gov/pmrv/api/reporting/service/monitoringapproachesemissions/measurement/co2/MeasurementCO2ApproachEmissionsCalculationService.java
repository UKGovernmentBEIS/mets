package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.co2;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Transfer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2Direction;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.MeasurementApproachEmissionsCalculationService;

import java.math.BigDecimal;

@Service
public class MeasurementCO2ApproachEmissionsCalculationService extends MeasurementApproachEmissionsCalculationService {

    private static final int NEGATIVE_AMOUNT = -1;

    @Override
    public BigDecimal getTotalEmissions(AerMonitoringApproachEmissions approachEmissions) {
        return ((MeasurementOfCO2Emissions) approachEmissions).getEmissionPointEmissions().stream()
            .map(this::getEmissionPointTotalReportableEmissions
            )
            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    @Override
    public MonitoringApproachType getType() {
        return MonitoringApproachType.MEASUREMENT_CO2;
    }

    @Override
    public BigDecimal calculateTransferredEmissions(Transfer transfer, BigDecimal emissions) {
        emissions = emissions.abs();
        return ((TransferCO2) transfer).getTransferDirection() != TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION ?
            emissions.multiply(BigDecimal.valueOf(NEGATIVE_AMOUNT)) : emissions;
    }
}
